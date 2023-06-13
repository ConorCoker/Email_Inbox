package com.example.emailinbox.ui

import androidx.lifecycle.ViewModel
import com.example.emailinbox.EmailFactory
import com.example.emailinbox.InboxEvent
import com.example.emailinbox.InboxState
import com.example.emailinbox.InboxStatus
import kotlinx.coroutines.flow.MutableStateFlow

class InboxViewModel:ViewModel() {

    val uiState = MutableStateFlow(InboxState())

    fun loadContent(){
        uiState.value = uiState.value.copy(
            status = InboxStatus.LOADING
        )
        uiState.value = uiState.value.copy(
            status = InboxStatus.SUCCESS,
            content = EmailFactory.makeEmailList()
        )
    }

    fun handleEvent(inboxEvent: InboxEvent){
        when(inboxEvent){
            is InboxEvent.RefreshContent -> loadContent()
            is InboxEvent.DeleteEmail -> deleteEmail(inboxEvent.id)
        }
    }

    private fun deleteEmail(id:String){
        uiState.value = uiState.value.copy(
            content = this.uiState.value.content?.filter {
                id != it.id   //this could be wrong as way from guide is deprecated
            }
        )
    }
}