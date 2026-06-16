package com.avirajsharma.zenki.ui.screens.deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avirajsharma.zenki.data.local.entity.Flashcard
import com.avirajsharma.zenki.ui.viewmodel.DeckDetailViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun DeckDetailScreen(
    deckId: Long,
    onBack: () -> Unit,
    onStudy: (Long) -> Unit,
    onQuiz: (Long) -> Unit,
    onAddCard: (Long) -> Unit,
) {
    val viewModel = koinInject<DeckDetailViewModel> { parametersOf(deckId) }
    val flashcards by viewModel.flashcards.collectAsStateWithLifecycle()
    val cardCount by viewModel.cardCount.collectAsStateWithLifecycle()

    DeckDetailContent(
        deckId = deckId,
        flashcards = flashcards,
        cardCount = cardCount,
        onBack = onBack,
        onStudy = onStudy,
        onQuiz = onQuiz,
        onAddCard = onAddCard,
        onDeleteCard = viewModel::deleteCard,
        onDeleteDeck = {
            viewModel.deleteDeck()
            onBack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckDetailContent(
    deckId: Long,
    flashcards: List<Flashcard>,
    cardCount: Int,
    onBack: () -> Unit,
    onStudy: (Long) -> Unit,
    onQuiz: (Long) -> Unit,
    onAddCard: (Long) -> Unit,
    onDeleteCard: (Flashcard) -> Unit,
    onDeleteDeck: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cards", "Study", "Quiz")
    var showDeleteDeckDialog by remember { mutableStateOf(false) }

    if (showDeleteDeckDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDeckDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            title = { Text("Delete deck?") },
            text = {
                Text("This deck and all $cardCount ${if (cardCount == 1) "card" else "cards"} inside will be permanently deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDeckDialog = false
                        onDeleteDeck()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDeckDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Deck") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDeckDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete deck")
                    }
                },
            )

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> CardsTab(flashcards, onDeleteCard)
                1 -> StudyTab(cardCount, { onStudy(deckId) })
                2 -> QuizTab(cardCount, { onQuiz(deckId) })
            }
        }

        if (selectedTab == 0) {
            FloatingActionButton(
                onClick = { onAddCard(deckId) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add card")
            }
        }
    }
}

@Composable
private fun CardsTab(
    flashcards: List<Flashcard>,
    onDeleteCard: (Flashcard) -> Unit,
) {
    if (flashcards.isEmpty()) {
        EmptyTabMessage("No cards in this deck yet")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(flashcards, key = { it.id }) { card ->
            CardListItem(card, onDeleteCard)
        }
    }
}

@Composable
private fun CardListItem(
    card: Flashcard,
    onDelete: (Flashcard) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            title = { Text("Delete card?") },
            text = { Text("\"${card.question}\" will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(card)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    ListItem(
        headlineContent = {
            Text(
                text = card.question,
                maxLines = 2,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        supportingContent = {
            Text(
                text = card.answer,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingContent = {
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

@Composable
private fun StudyTab(
    cardCount: Int,
    onStartStudy: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "$cardCount cards ready",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Review your flashcards with flip animations",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Button(
            onClick = onStartStudy,
            enabled = cardCount > 0,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Start Studying")
        }
    }
}

@Composable
private fun QuizTab(
    cardCount: Int,
    onStartQuiz: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Quiz,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Test your knowledge",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "$cardCount cards available for quiz",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Button(
            onClick = onStartQuiz,
            enabled = cardCount >= 4,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Start Quiz")
        }
    }
}

@Composable
private fun EmptyTabMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
