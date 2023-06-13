package com.example.emailinbox

sealed class InboxEvent{
    object RefreshContent:InboxEvent()
    class DeleteEmail(val id:String):InboxEvent()
}
