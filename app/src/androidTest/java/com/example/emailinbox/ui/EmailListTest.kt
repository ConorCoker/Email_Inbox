package com.example.emailinbox.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import com.example.emailinbox.EmailFactory
import com.example.emailinbox.tags.Tags
import org.junit.Rule
import org.junit.Test

class EmailListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun email_items_displayed(){
        val emails = EmailFactory.makeEmailList()
        composeTestRule.setContent {
            EmailList(emails = emails)
        }

        emails.forEachIndexed { index, email ->
            composeTestRule.onNodeWithTag(Tags.TAG_CONTENT).
                    onChildAt(index).performScrollTo().assert(hasTestTag(Tags.TAG_EMAIL + email.id))
        }
    }

}