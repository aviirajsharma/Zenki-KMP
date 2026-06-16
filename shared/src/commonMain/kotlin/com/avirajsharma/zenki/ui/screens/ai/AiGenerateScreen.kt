package com.avirajsharma.zenki.ui.screens.ai

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avirajsharma.zenki.currentTimeMillis
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.ui.viewmodel.AIGenerateUiState
import com.avirajsharma.zenki.ui.viewmodel.AIGenerateViewModel
import org.koin.compose.koinInject
import kotlin.math.sin
import kotlin.random.Random

private const val CONFETTI_PARTICLE_COUNT = 80
private const val CONFETTI_ANIMATION_DURATION_MS = 3500
private const val CONFETTI_FADE_START_THRESHOLD = 0.8f

private val confettiPalette = listOf(
    Color(0xFFE91E63),
    Color(0xFF2196F3),
    Color(0xFF4CAF50),
    Color(0xFFFFC107),
    Color(0xFF9C27B0),
    Color(0xFFFF5722),
    Color(0xFF00BCD4),
)

@Composable
fun AIGenerateScreen(
    viewModel: AIGenerateViewModel = koinInject(),
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateToDeck: (Long) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AIGenerateContent(
        state = state,
        contentPadding = contentPadding,
        onTopicChange = viewModel::updateTopic,
        onCountChange = viewModel::updateCardCount,
        onGenerate = viewModel::generate,
        onSave = { viewModel.saveToNewDeck("AI Deck") },
        onDismissError = viewModel::dismissError,
        onNavigateToDeck = onNavigateToDeck,
    )
}

@Composable
private fun AIGenerateContent(
    state: AIGenerateUiState,
    contentPadding: PaddingValues,
    onTopicChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onGenerate: () -> Unit,
    onSave: () -> Unit,
    onDismissError: () -> Unit,
    onNavigateToDeck: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state) {
            is AIGenerateUiState.Idle -> IdleView(state, onTopicChange, onCountChange, onGenerate)
            is AIGenerateUiState.Loading -> LoadingView()
            is AIGenerateUiState.Preview -> PreviewView(state.cards, onSave)
            is AIGenerateUiState.Error -> ErrorView(state.message, onDismissError)
            is AIGenerateUiState.Saved -> SavedView(state.deckId, onNavigateToDeck)
        }
    }
}

@Composable
private fun IdleView(
    state: AIGenerateUiState.Idle,
    onTopicChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onGenerate: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AI Flashcard Generator", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.topic,
            onValueChange = onTopicChange,
            label = { Text("Topic or text") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Column {
            Text("Cards: ${state.cardCount}")
            Slider(
                value = state.cardCount.toFloat(),
                onValueChange = { onCountChange(it.toInt()) },
                valueRange = 5f..20f,
                steps = 14,
            )
        }

        if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Button(onClick = onGenerate, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Text("Generate", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Text("Generating flashcards...", modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun PreviewView(cards: List<Flashcard>, onSave: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Generated ${cards.size} cards",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(cards) { card -> GeneratedCardItem(card) }
        }

        FilledTonalButton(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Add to Deck")
        }
    }
}

@Composable
private fun GeneratedCardItem(card: Flashcard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(card.question, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = card.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun ErrorView(message: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
            Text("Try Again")
        }
    }
}

@Composable
private fun SavedView(deckId: Long, onNavigateToDeck: (Long) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        ConfettiOverlay()
        SavedContent(onExploreDeck = { onNavigateToDeck(deckId) })
    }
}

@Composable
private fun SavedContent(onExploreDeck: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Text("Cards saved!", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Your AI-generated deck is ready to study.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onExploreDeck, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Explore Deck")
        }
    }
}

@Composable
private fun ConfettiOverlay() {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = CONFETTI_ANIMATION_DURATION_MS, easing = LinearEasing),
        )
    }

    val particles = remember { spawnParticles() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawParticle(particle, animationProgress.value)
        }
    }
}

private fun spawnParticles(): List<Particle> {
    val random = Random(currentTimeMillis())
    return List(CONFETTI_PARTICLE_COUNT) {
        Particle(
            startX = random.nextFloat(),
            startY = -random.nextFloat() * 0.3f,
            velocityX = (random.nextFloat() - 0.5f) * 0.003f,
            velocityY = 0.003f + random.nextFloat() * 0.004f,
            color = confettiPalette[random.nextInt(confettiPalette.size)],
            width = 6f + random.nextFloat() * 8f,
            initialRotation = random.nextFloat() * 360f,
            rotationSpeed = (random.nextFloat() - 0.5f) * 6f,
            wobblePhase = random.nextFloat() * 6.28f,
        )
    }
}

private fun DrawScope.drawParticle(particle: Particle, progress: Float) {
    val screenX = computeScreenX(particle, progress)
    val screenY = computeScreenY(particle, progress)

    if (isFallenOffScreen(screenY)) return

    val rotation = particle.initialRotation + particle.rotationSpeed * progress * 60f
    val opacity = fadeOpacity(screenY / size.height)

    rotate(degrees = rotation, pivot = Offset(screenX, screenY)) {
        drawRect(
            color = particle.color.copy(alpha = opacity),
            topLeft = Offset(screenX - particle.width / 2f, screenY - particle.width / 4f),
            size = Size(particle.width, particle.width / 2f),
        )
    }
}

private fun DrawScope.computeScreenX(particle: Particle, progress: Float): Float {
    val normalizedX = particle.startX + particle.velocityX * progress * 300f +
            sin(progress * 4f + particle.wobblePhase) * 0.015f
    return normalizedX.coerceIn(0f, 1f) * size.width
}

private fun DrawScope.computeScreenY(particle: Particle, progress: Float): Float =
    (particle.startY + particle.velocityY * progress * 300f) * size.height

private fun isFallenOffScreen(screenY: Float): Boolean = screenY > 0f

private fun fadeOpacity(normalizedY: Float): Float =
    if (normalizedY > CONFETTI_FADE_START_THRESHOLD) {
        ((1f - normalizedY) / (1f - CONFETTI_FADE_START_THRESHOLD)).coerceIn(0f, 1f)
    } else {
        1f
    }

private data class Particle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val width: Float,
    val initialRotation: Float,
    val rotationSpeed: Float,
    val wobblePhase: Float,
)
