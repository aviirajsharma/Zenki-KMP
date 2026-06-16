package com.avirajsharma.zenki.data.remote

import com.avirajsharma.zenki.data.local.entity.Flashcard
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GroqService(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun generateFlashcards(topic: String, count: Int): List<Flashcard> {
        val request = ChatCompletionRequest(
            model = MODEL,
            messages = listOf(
                Message(role = "system", content = SYSTEM_PROMPT),
                Message(role = "user", content = buildPrompt(topic, count)),
            ),
        )

        val httpResponse: HttpResponse = httpClient.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(request)
        }

        when (httpResponse.status) {
            HttpStatusCode.Unauthorized -> throw GroqError.ApiError(401, "Invalid API key")
            HttpStatusCode.TooManyRequests -> throw GroqError.ApiError(429, "Rate limit exceeded — try again shortly")
            HttpStatusCode.InternalServerError -> throw GroqError.ApiError(500, "Groq server error")
            else -> if (!httpResponse.status.value.toString().startsWith("2")) {
                throw GroqError.ApiError(httpResponse.status.value, "Unexpected error (${httpResponse.status.value})")
            }
        }

        val response: ChatCompletionResponse = httpResponse.body()
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw GroqError.EmptyResponse

        return parseFlashcards(content)
    }

    private fun buildPrompt(topic: String, count: Int): String =
        """
        Create exactly $count flashcards on the topic below.
        Return ONLY a JSON object with a "flashcards" array.
        Each item must have "question" and "answer" fields.

        Topic: $topic
        """.trimIndent()

    private fun parseFlashcards(rawContent: String): List<Flashcard> {
        // Extract the JSON object from the response (model may add surrounding text)
        val start = rawContent.indexOf('{')
        val end = rawContent.lastIndexOf('}')
        if (start == -1 || end == -1 || end <= start) throw GroqError.InvalidJson

        val jsonString = rawContent.substring(start, end + 1)
        val wrapper = try {
            json.decodeFromString<FlashcardWrapper>(jsonString)
        } catch (e: Exception) {
            throw GroqError.InvalidJson
        }
        return wrapper.flashcards.map { draft ->
            Flashcard(deckId = 0, question = draft.question, answer = draft.answer)
        }
    }

    companion object {
        private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
        private const val MODEL = "llama-3.3-70b-versatile"
        private const val SYSTEM_PROMPT =
            "You are a flashcard generator. You respond ONLY with valid JSON containing a 'flashcards' array."
    }
}

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    val message: Message,
)

@Serializable
data class FlashcardWrapper(
    val flashcards: List<FlashcardDraft>,
)

@Serializable
data class FlashcardDraft(
    val question: String,
    val answer: String,
)

sealed class GroqError(message: String) : Exception(message) {
    data object EmptyResponse : GroqError("AI returned empty response")
    data object InvalidJson : GroqError("AI response was not valid JSON")
    data class ApiError(val code: Int, val detail: String = "API error: $code") : GroqError(detail)
}
