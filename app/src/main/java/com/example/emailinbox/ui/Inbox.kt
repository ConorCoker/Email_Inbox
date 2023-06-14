package com.example.emailinbox.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Inbox() {
    MaterialTheme {
        val viewModel: InboxViewModel = viewModel()
        EmailInbox(
            modifier = Modifier.fillMaxWidth(),
            inboxState = viewModel.uiState.collectAsState().value,
            inboxEventListener = viewModel::handleEvent
        )
        LaunchedEffect(Unit) {
            viewModel.loadContent()
        }
    }
}
