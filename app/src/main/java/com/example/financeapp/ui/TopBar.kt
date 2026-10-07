@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.financeapp.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Topbar(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        TopAppBar(
            title = {
                Text(text = "Finance App")
            },
            actions = { },
            modifier = modifier
        )
    }
}
