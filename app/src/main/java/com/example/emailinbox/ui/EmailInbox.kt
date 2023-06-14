package com.example.emailinbox.ui

import android.annotation.SuppressLint
import android.graphics.Path.Direction
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.emailinbox.EmailFactory
import com.example.emailinbox.InboxEvent
import com.example.emailinbox.InboxState
import com.example.emailinbox.InboxStatus
import com.example.emailinbox.R
import com.example.emailinbox.models.Email
import com.example.emailinbox.tags.Tags
import kotlinx.coroutines.delay
import java.nio.file.WatchEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInbox(
    modifier: Modifier = Modifier,
    inboxState: InboxState,
    inboxEventListener: (inboxEvent: InboxEvent) -> Unit) {
    Scaffold(modifier = modifier, topBar = {
        AppTopAppBar {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                text = stringResource(
                    id = R.string.title_inbox, inboxState.content?.count() ?: 0
                )
            )
        }
    }) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             when(inboxState.status) {
                 InboxStatus.LOADING -> Loading()
                 InboxStatus.HAS_EMAILS -> EmailList(emails = inboxState.content?:emptyList(),inboxEventListener = inboxEventListener)
                 InboxStatus.EMPTY -> EmptyState(inboxEventListener = inboxEventListener)
                 InboxStatus.SUCCESS -> EmailList(emails = inboxState.content?:emptyList(),inboxEventListener = inboxEventListener)
                 else -> {
                     ErrorState(inboxEventListener = inboxEventListener) //this else is always running during tests
                 }
             }
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier.testTag(Tags.TAG_PROGRESS))
}

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    inboxEventListener: (inboxEvent: InboxEvent) -> Unit
) {
    Column(modifier = modifier
        .padding(16.dp)
        .testTag(Tags.TAG_ERROR), horizontalAlignment = Alignment.CenterHorizontally) {
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
    Column(modifier = modifier
        .padding(16.dp)
        .testTag(Tags.TAG_EMPTY), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(id = R.string.empty_inbox_message))
        Spacer(modifier = modifier.height(12.dp))
        Button(onClick = {
            inboxEventListener(InboxEvent.RefreshContent)
        }) {
            Text(text = stringResource(id = R.string.label_check_again))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmailList(
    modifier: Modifier = Modifier,
    emails: List<Email>,
    inboxEventListener: (inboxEvent: InboxEvent) -> Unit = {}
) {





    val deleteEmailLabel = stringResource(id = R.string.cd_delete_email)



    LazyColumn(modifier = modifier.testTag(Tags.TAG_CONTENT)) {
        items(emails, key = { email ->
            email.id
        }) { email ->
            var isEmailItemDismissed by remember { mutableStateOf(false) }

            val dismissState = rememberDismissState {
                if (it == DismissValue.DismissedToEnd) {
                    isEmailItemDismissed = true
                    inboxEventListener(InboxEvent.DeleteEmail(email.id))
                }
                true
            }
            val dividerVisibilityAnimation by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.Default) {
                    1f
                } else 0f, animationSpec = tween(delayMillis = 300)
            )
            val emailHeightAnimation by animateDpAsState(
                targetValue = if (isEmailItemDismissed.not()) {
                    120.dp
                } else 0.dp, animationSpec = tween(delayMillis = 300), finishedListener = {
                    inboxEventListener(InboxEvent.DeleteEmail(email.id))
                }
            )
            androidx.compose.material.SwipeToDismiss(
                modifier = Modifier.semantics {
                    customActions = listOf(
                        CustomAccessibilityAction(label = deleteEmailLabel) {
                            inboxEventListener(InboxEvent.DeleteEmail(email.id))
                            true
                        }
                    )
                }, directions = setOf(DismissDirection.StartToEnd),
                dismissThresholds = { FractionalThreshold(0.15f) },
                background = {
                    EmailItemBackground(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(emailHeightAnimation), dismissState = dismissState
                    )
                },
                state = dismissState
            ){
                EmailItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(emailHeightAnimation),
                    email = email,
                    dismissState = dismissState
                )
            }
            Divider(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .alpha(dividerVisibilityAnimation)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmailItem(modifier: Modifier = Modifier, email: Email, dismissState: DismissState) {
    val cardElevation by animateDpAsState(
        targetValue = if (dismissState.dismissDirection != null) {
            4.dp
        } else 0.dp
    )
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(cardElevation), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmailItemBackground(modifier: Modifier = Modifier, dismissState: DismissState) {
    val backgroundColor by animateColorAsState(
        targetValue = when (
            dismissState.targetValue
        ) {
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.background
        }, animationSpec = tween()
    )

    val iconColor by animateColorAsState(
        targetValue = when (
            dismissState.targetValue
        ) {
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.error
        }, animationSpec = tween()
    )

    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) {
            1f
        } else 0.50f
    )

    Box(modifier = modifier.background(backgroundColor)) {
        if (dismissState.targetValue == DismissValue.DismissedToEnd) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .scale(scale)
                    .padding(start = 10.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.cd_delete_email),
                tint = iconColor
            )
        }
    }

}