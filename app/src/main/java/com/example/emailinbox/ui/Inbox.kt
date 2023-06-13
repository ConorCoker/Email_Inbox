package com.example.emailinbox.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Inbox(){
    MaterialTheme {
        val viewModel:InboxViewModel = viewModel()
        EmailInbox(inboxState = viewModel.uiState.collectAsState().value, inboxEventListener = viewModel::handleEvent )
        LaunchedEffect(Unit){
            viewModel.loadContent()
        }
    }
}
