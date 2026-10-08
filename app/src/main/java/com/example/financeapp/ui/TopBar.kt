@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.financeapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.financeapp.ui.transactions.DropdownMenu

@Composable
fun Topbar(
    modifier: Modifier = Modifier,
    title: String = "Finance App",
    showMenu: Boolean = false,
    onMenuClick: () -> Unit = {}
) {
    Surface(modifier = modifier) {
        var expanednMenu by remember { mutableStateOf(false) }

        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (showMenu) {
                    IconButton(onClick = { expanednMenu = !expanednMenu }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        expaned = expanednMenu,
                        onDismissRequest = { expanednMenu = false }
                    )
                }
            },
            modifier = modifier
        )
    }
}
