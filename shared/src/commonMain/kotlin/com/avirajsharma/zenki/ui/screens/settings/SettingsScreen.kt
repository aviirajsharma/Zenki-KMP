package com.avirajsharma.zenki.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avirajsharma.zenki.data.repository.ThemeModeRepository
import com.avirajsharma.zenki.theme.ThemeMode
import org.koin.compose.koinInject

@Composable
fun SettingsScreen() {
    val repository = koinInject<ThemeModeRepository>()
    val themeMode by repository.observeThemeMode()
        .collectAsStateWithLifecycle(ThemeMode.System)

    SettingsContent(
        currentMode = themeMode,
        onModeToggle = { mode ->
            // Fire-and-forget save via a simple remember scope
        },
        onSaveMode = { mode ->
            kotlinx.coroutines.runBlocking {
                repository.saveThemeMode(mode)
            }
        },
    )
}

@Composable
private fun SettingsContent(
    currentMode: ThemeMode,
    onModeToggle: (ThemeMode) -> Unit,
    onSaveMode: (ThemeMode) -> Unit,
) {
    var showAbout by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        SettingsSectionTitle("Appearance")

        ThemeListItem(
            label = "Light",
            icon = Icons.Default.LightMode,
            selected = currentMode == ThemeMode.Light,
            onClick = { onSaveMode(ThemeMode.Light) },
        )
        ThemeListItem(
            label = "Dark",
            icon = Icons.Default.DarkMode,
            selected = currentMode == ThemeMode.Dark,
            onClick = { onSaveMode(ThemeMode.Dark) },
        )
        ThemeListItem(
            label = "System default",
            icon = Icons.Default.SettingsSuggest,
            selected = currentMode == ThemeMode.System,
            onClick = { onSaveMode(ThemeMode.System) },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SettingsSectionTitle("About")

        ListItem(
            headlineContent = { Text("About Zenki") },
            leadingContent = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            modifier = Modifier.clickable { showAbout = true },
        )

        ListItem(
            headlineContent = { Text("Help & Feedback") },
            leadingContent = {
                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null)
            },
            modifier = Modifier.clickable { },
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppVersionFooter()

        if (showAbout) {
            AboutDialog(onDismiss = { showAbout = false })
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun ThemeListItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            Switch(
                checked = selected,
                onCheckedChange = { onClick() },
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}

@Composable
private fun AppVersionFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Zenki",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "About Zenki",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "Zenki is a flashcard study buddy app built with Compose Multiplatform. Create decks, study with flip cards, test yourself with quizzes, and generate cards with AI.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
