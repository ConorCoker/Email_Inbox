package com.example.emailinbox.ui

import androidx.compose.runtime.withRunningRecomposer
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.platform.app.InstrumentationRegistry
import com.example.emailinbox.EmailFactory
import com.example.emailinbox.InboxState
import com.example.emailinbox.InboxStatus
import org.junit.Rule
import org.junit.Test
import com.example.emailinbox.R
import com.example.emailinbox.tags.Tags

class EmailInboxTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun inbox_Title_Displayed(){
        val inboxState = InboxState(content = EmailFactory.makeEmailList())

        composeTestRule.setContent {
            EmailInbox(inboxState = inboxState){}
        }

        composeTestRule.onNodeWithText(InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.title_inbox,inboxState.content!!.count())).assertIsDisplayed()
    }

    @Test
    fun loading_is_displayed_when_loading(){

        composeTestRule.run {
            setContent {EmailInbox(inboxState = InboxState(status = InboxStatus.LOADING)){} }
            onNodeWithTag(Tags.TAG_PROGRESS).assertIsDisplayed()
            onNodeWithTag(Tags.TAG_CONTENT).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_ERROR).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_EMPTY).assertDoesNotExist()
        }
    }

    @Test
    fun empty_state_is_displayed_when_list_empty(){

        composeTestRule.run {
            setContent {EmailInbox(inboxState = InboxState(status = InboxStatus.EMPTY)){} }
            onNodeWithTag(Tags.TAG_PROGRESS).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_CONTENT).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_ERROR).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_EMPTY).assertIsDisplayed()
        }
    }

    @Test
    fun error_state_is_displayed_when_error(){

        composeTestRule.run {
            setContent {EmailInbox(inboxState = InboxState(status = InboxStatus.ERROR)){} }
            onNodeWithTag(Tags.TAG_PROGRESS).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_CONTENT).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_ERROR).assertIsDisplayed()
            onNodeWithTag(Tags.TAG_EMPTY).assertDoesNotExist()
        }
    }

    @Test
    fun content_state_is_displayed_when_content_exists(){

        composeTestRule.run {
            setContent {EmailInbox(inboxState = InboxState(status = InboxStatus.SUCCESS)){} }
            onNodeWithTag(Tags.TAG_PROGRESS).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_CONTENT).assertExists()
            onNodeWithTag(Tags.TAG_ERROR).assertDoesNotExist()
            onNodeWithTag(Tags.TAG_EMPTY).assertDoesNotExist()
        }
    }

    @Test
    fun item_dismissed_when_swiped(){
        composeTestRule.setContent {
            Inbox()
        }
        composeTestRule.onNodeWithTag(Tags.TAG_CONTENT).onChildAt(0).performTouchInput {
            swipeRight()
        } //failing too
        val emails = EmailFactory.makeEmailList()
        composeTestRule.onNodeWithTag(Tags.TAG_CONTENT).onChildren().assertCountEquals(emails.count() - 1)
    }

}