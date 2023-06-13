package com.example.emailinbox.ui

import android.annotation.SuppressLint
import android.graphics.Path.Direction
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.emailinbox.InboxEvent
import com.example.emailinbox.InboxState
import com.example.emailinbox.InboxStatus
import com.example.emailinbox.R
import com.example.emailinbox.models.Email
import java.nio.file.WatchEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EmailInbox(
    modifier: Modifier = Modifier,
    inboxState: InboxState = InboxState(status = InboxStatus.LOADING, content = emptyList()),
    inboxEventListener: (inboxEvent: InboxEvent) -> Unit = {}
) {
    Scaffold(modifier = modifier, topBar = {
        AppTopAppBar {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                text = stringResource(
                    id = R.string.title_inbox, inboxState.content!!.count()
                )
            )
        }
    }) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (inboxState.status == InboxStatus.LOADING) {
                Loading()
            } else if (inboxState.status == InboxStatus.ERROR) {
                ErrorState() {
                    inboxEventListener(InboxEvent.RefreshContent)
                }
            } else {
                EmptyState() {
                    inboxEventListener(InboxEvent.RefreshContent)
                }
            }


        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    inboxEventListener: (inboxEvent: InboxEvent) -> Unit
) {
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(id = R.string.message_content_error))
        Spacer(modifier = modifier.height(12.dp))
        Button(onClick = {
            inboxEventListener(InboxEvent.RefreshContent)
        }) {
            Text(text = stringResource(id = R.string.label_try_again))
        }
    }


}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    inboxEventListener: (inboxEvent: InboxEvent) -> Unit = {}
) {
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(id = R.string.empty_inbox_message))
        Spacer(modifier = modifier.height(12.dp))
        Button(onClick = {
            inboxEventListener(InboxEvent.RefreshContent)
        }) {
            Text(text = stringResource(id = R.string.label_check_again))
        }
    }
}

@Preview
@Composable
fun EmailList(modifier: Modifier = Modifier, emails: List<Email> = emptyList()) {
    LazyColumn(modifier = modifier) {
        items(emails, key = { email ->
            email.id
        }) {

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismiss(
    directions: Set<DismissDirection> = setOf(DismissDirection.StartToEnd),
    dismissThresholds: () -> FractionalThreshold = {
        FractionalThreshold(0.15f)
    },
    dismissContent: (email: Email) -> Unit = {
        //needs attention page 614
    },background: () -> Unit = {
//        EmailItemBackground()
    }
) {

}

@Preview
@Composable
fun EmailItem(modifier: Modifier = Modifier, email: Email = Email("", "Title", "Description")) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = email.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = email.description,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}

@Composable
fun EmailItemBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(20.dp)) {
        Icon(
            modifier = Modifier.align(Alignment.CenterStart),
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.cd_delete_email)
        )
    }

}