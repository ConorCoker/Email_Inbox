package com.example.emailinbox

import com.example.emailinbox.models.Email

data class InboxState(var status: InboxStatus = InboxStatus.LOADING, val content:List<Email>?=null)
