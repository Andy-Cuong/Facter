package com.andyc.checker.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andyc.checker.presentation.R
import com.andyc.checker.presentation.list.model.ChatUi
import com.andyc.core.presentation.designsystem.FacterTheme
import com.andyc.core.presentation.designsystem.components.FacterDialog
import com.andyc.core.presentation.designsystem.components.util.SwipeableItem

@Composable
fun ChatListItem(
    chatUi: ChatUi,
    onChatClicked: (String) -> Unit,
    onDeleteChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteConfirmed by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        FacterDialog(
            title = stringResource(R.string.confirm_delete_chat),
            description = stringResource(R.string.action_cannot_be_undone),
            onDismissRequest = { showDeleteDialog = false },
            primaryButton = {
                Button(
                    onClick = {
                        deleteConfirmed = true
                        showDeleteDialog = false
                        onDeleteChat(chatUi.id)
                    }
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            secondaryButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    SwipeableItem(
        item = chatUi,
        onDelete = { showDeleteDialog = true },
        isRevealed = false,
        deleteConfirmed = deleteConfirmed
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = chatUi.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .clickable { onChatClicked(chatUi.id) },
                supportingContent = {
                    Text(
                        text = chatUi.lastMessage,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                trailingContent = {
                    Text(
                        text = chatUi.lastMessageTime,
                        fontWeight = FontWeight.Light
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatListItemPrev() {
    FacterTheme {
        ChatListItem(
            chatUi = ChatUi(
                id = "abc",
                userId = "user1",
                title = "Does dragon exist?",
                lastMessageTime = "08:45",
                lastMessage = "...That depends on what you think is a dragon"
            ),
            onChatClicked = {},
            onDeleteChat = {}
        )
    }
}
