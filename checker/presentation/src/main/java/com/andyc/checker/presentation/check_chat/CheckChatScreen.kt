@file:OptIn(ExperimentalMaterial3Api::class)

package com.andyc.checker.presentation.check_chat

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andyc.checker.domain.Message
import com.andyc.checker.domain.MessageRole
import com.andyc.checker.presentation.R
import com.andyc.checker.presentation.check_chat.components.MessageListItem
import com.andyc.checker.presentation.check_chat.mapper.toMessageUi
import com.andyc.core.presentation.designsystem.FacterTheme
import com.andyc.core.presentation.designsystem.components.FacterDialog
import com.andyc.core.presentation.designsystem.components.FacterTextField
import com.andyc.core.presentation.designsystem.components.FacterTopBar
import com.andyc.core.presentation.ui.NavigationDestination
import com.andyc.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

object CheckChatDestination: NavigationDestination {
    override val route = "check_chat"
    const val CHAT_ID_ARG = "chat_id"
    val routeWithArgs = "$route/{$CHAT_ID_ARG}"
}

@Composable
fun CheckChatScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: CheckChatViewModel = koinViewModel()
) {
    var showRetryDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CheckChatEvent.ChatLoaded -> { showRetryDialog = false }
            CheckChatEvent.FailedToLoadChat -> { showRetryDialog = true }
            CheckChatEvent.FailedToUpsertChat -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_failed_to_sync_chat),
                    Toast.LENGTH_LONG
                ).show()
            }
            CheckChatEvent.FailedToGetResponse -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_failed_to_get_answer),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    if (showRetryDialog) {
        FacterDialog(
            title = stringResource(R.string.error_failed_to_load_chat),
            description = stringResource(R.string.ask_try_again),
            onDismissRequest = {},
            primaryButton = {
                Button(
                    onClick = { viewModel.onAction(CheckChatAction.OnLoadChatClick) }
                ) {
                    Text(stringResource(R.string.try_again))
                }
            },
            secondaryButton = {
                OutlinedButton(
                    onClick = { onNavigateBack() }
                ) {
                    Text(stringResource(R.string.go_back))
                }
            }
        )
    }

    CheckChatScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                CheckChatAction.OnLoadChatClick -> Unit
                is CheckChatAction.OnSendMessage -> Unit
                CheckChatAction.OnNavigateBack -> {
                    onNavigateBack()
                }
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun CheckChatScreen(
    state: CheckChatState,
    onAction: (CheckChatAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            FacterTopBar(
                showBackButton = true,
                title = state.title,
                onBackClick = { onAction(CheckChatAction.OnNavigateBack) },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues)
                .imePadding()
        ) {
            val lazyColumnState = LazyListState()
            var textFieldHeight by rememberSaveable { mutableIntStateOf(0) }
            val density = LocalDensity.current
            val bottomPadding = with(density) {
                textFieldHeight.toDp()
            }

            LaunchedEffect(state.messages.size) {
                lazyColumnState.scrollToItem((state.messages.size - 1).coerceAtLeast(0))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomPadding),
                state = lazyColumnState,
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = state.messages, key = { message -> message.id }) {
                    MessageListItem(
                        message = it.toMessageUi(),
                        userImageUrl = state.user?.photoUrl,
                    )
                }
            }

            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }

            FacterTextField(
                state = state.messageToSend,
                hint = stringResource(R.string.hint_text_field),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged {
                        with(density) {
                            textFieldHeight = it.height
                        }
                    }
                ,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                onKeyboardAction = {
                    onAction(CheckChatAction.OnSendMessage(state.messageToSend.text.toString()))
                },
                endButton = {
                    IconButton(
                        onClick = {
                            onAction(CheckChatAction.OnSendMessage(state.messageToSend.text.toString()))
                        },
                        enabled = state.canSend
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = stringResource(R.string.send),
                            tint = if (state.canSend) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckChatScreenPrev() {
    FacterTheme {
        CheckChatScreen(
            state = CheckChatState(
                chatId = "chat1",
                title = "Sample title",
                user = null,
                messageToSend = TextFieldState("Is the Moon flat?"),
                canSend = true,
                isFirstMessage = false,
                messages = listOf(
                    Message(
                        id = "chat1",
                        role = MessageRole.USER,
                        sentEpochSecond = 1756315895,
                        content = "Message sent by user. Gotta make this long so as to be seen in preview"
                    ),
                    Message(
                        id = "chat2",
                        role = MessageRole.ASSISTANT,
                        sentEpochSecond = 1722631589,
                        content = "Message by system. This is short and concise"
                    ),
                    Message(
                        id = "chat3",
                        role = MessageRole.USER,
                        sentEpochSecond = 1744631589,
                        content = "Short user message"
                    ),
                )
            ),
            onAction = {}
        )
    }
}