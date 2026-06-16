package com.avirajsharma.zenki.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.data.repository.DeckRepository
import com.avirajsharma.zenki.data.repository.FlashcardRepository
import com.avirajsharma.zenki.data.repository.StudySessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizViewModel(
    private val deckId: Long,
    private val deckRepository: DeckRepository,
    private val flashcardRepository: FlashcardRepository,
    private val sessionRepository: StudySessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var questions: List<QuizQuestion> = emptyList()
    private var correctCount = 0

    init {
        viewModelScope.launch {
            val cards = flashcardRepository.observeFlashcards(deckId).first()
            questions = buildQuestions(cards)
            if (questions.isNotEmpty()) {
                _uiState.value = QuizUiState.Question(
                    question = questions.first(),
                    currentIndex = 0,
                    total = questions.size,
                    score = 0,
                )
            } else {
                _uiState.value = QuizUiState.Empty
            }
        }
    }

    fun selectAnswer(answerIndex: Int) {
        val state = _uiState.value as? QuizUiState.Question ?: return
        val question = state.question
        val isCorrect = answerIndex == question.correctIndex
        if (isCorrect) correctCount++

        _uiState.value = QuizUiState.Answered(
            question = question,
            selectedIndex = answerIndex,
            isCorrect = isCorrect,
            currentIndex = state.currentIndex,
            total = state.total,
            score = correctCount,
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        val currentIndex = when (state) {
            is QuizUiState.Question -> state.currentIndex
            is QuizUiState.Answered -> state.currentIndex
            else -> return
        }
        val nextIndex = currentIndex + 1
        if (nextIndex >= questions.size) {
            finishQuiz()
            return
        }
        _uiState.value = QuizUiState.Question(
            question = questions[nextIndex],
            currentIndex = nextIndex,
            total = questions.size,
            score = correctCount,
        )
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            sessionRepository.recordSession(
                deckId = deckId,
                cardsStudied = questions.size,
                correctCount = correctCount,
            )
            deckRepository.markStudied(deckId)
        }
        _uiState.value = QuizUiState.Results(
            total = questions.size,
            correct = correctCount,
            percentage = if (questions.isEmpty()) 0 else (correctCount * 100 / questions.size),
        )
    }

    fun retryMistakes() {
        viewModelScope.launch {
            val cards = flashcardRepository.findUnknownCards(deckId)
            questions = buildQuestions(cards)
            correctCount = 0
            if (questions.isNotEmpty()) {
                _uiState.value = QuizUiState.Question(
                    question = questions.first(),
                    currentIndex = 0,
                    total = questions.size,
                    score = 0,
                )
            } else {
                _uiState.value = QuizUiState.Empty
            }
        }
    }
}

private fun buildQuestions(cards: List<Flashcard>): List<QuizQuestion> {
    if (cards.size < 4) return emptyList()
    return cards.shuffled().take(10).map { card ->
        val wrongAnswers = cards.filter { it.id != card.id }
            .shuffled()
            .take(3)
            .map { it.answer }
        val options = (wrongAnswers + card.answer).shuffled()
        QuizQuestion(
            question = card.question,
            options = options,
            correctIndex = options.indexOf(card.answer),
        )
    }
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
)

sealed class QuizUiState {
    data object Loading : QuizUiState()
    data object Empty : QuizUiState()
    data class Question(
        val question: QuizQuestion,
        val currentIndex: Int,
        val total: Int,
        val score: Int,
    ) : QuizUiState()
    data class Answered(
        val question: QuizQuestion,
        val selectedIndex: Int,
        val isCorrect: Boolean,
        val currentIndex: Int,
        val total: Int,
        val score: Int,
    ) : QuizUiState()
    data class Results(
        val total: Int,
        val correct: Int,
        val percentage: Int,
    ) : QuizUiState()
}
