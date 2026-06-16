package com.avirajsharma.zenki.ui.core

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppBottomNavigation(
    selectedTab: TopLevelTab,
    onTabSelected: (TopLevelTab) -> Unit,
) {
    NavigationBar {
        TopLevelTab.entries.forEach { tab ->
            NavigationBarItem(
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}
