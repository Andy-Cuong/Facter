@file:OptIn(ExperimentalMaterial3Api::class)

package com.andyc.checker.presentation.list

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.andyc.checker.presentation.R
import com.andyc.checker.presentation.list.components.ChatListItem
import com.andyc.checker.presentation.list.model.ChatUi
import com.andyc.core.domain.user.User
import com.andyc.core.presentation.designsystem.FacterTheme
import com.andyc.core.presentation.designsystem.components.FacterDialog
import com.andyc.core.presentation.designsystem.components.FacterTopBar
import com.andyc.core.presentation.designsystem.components.util.DropDownItem
import com.andyc.core.presentation.ui.NavigationDestination
import com.andyc.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

object ListDestination : NavigationDestination {
    override val route = "list"
}

@Composable
fun ListScreenRoot(
    onChatClick: (String) -> Unit,
    onSignOut: () -> Unit,
    viewModel: ListViewModel = koinViewModel()
) {
    var showRetryDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ListEvent.SignedOut -> onSignOut()
            ListEvent.FetchedChatHistory -> { showRetryDialog = false }
            ListEvent.FailedToFetchChatHistory -> { showRetryDialog = true }
            ListEvent.DeletedChat -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
            ListEvent.FailedToDeleteChat -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_failed_to_delete_chat),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    if (showRetryDialog) {
        FacterDialog(
            title = stringResource(R.string.error_failed_to_fetch_check_history),
            description = stringResource(R.string.ask_try_again),
            onDismissRequest = {},
            primaryButton = {
                Button(
                    onClick = { viewModel.onAction(ListAction.OnFetchChatHistory) }
                ) {
                    Text(text = stringResource(R.string.try_again))
                }
            }
        )
    }

    ListScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                ListAction.OnSignOutClick -> Unit
                ListAction.OnDeleteUserClick -> Unit
                is ListAction.OnChatClick -> onChatClick(action.chatId)
                is ListAction.OnDeleteChatClick -> Unit
                ListAction.OnFetchChatHistory -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ListScreen(
    state: ListState,
    onAction: (ListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSignOutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }

    if (showSignOutDialog) {
        FacterDialog(
            title = stringResource(R.string.confirm_sign_out),
            description = "",
            onDismissRequest = { showSignOutDialog = false },
            primaryButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onAction(ListAction.OnSignOutClick)
                    }
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }
            },
            secondaryButton = {
                OutlinedButton(
                    onClick = { showSignOutDialog = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        FacterDialog(
            title = stringResource(R.string.confirm_delete_account),
            description = stringResource(R.string.description_delete_account),
            onDismissRequest = { showDeleteAccountDialog = false },
            primaryButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onAction(ListAction.OnDeleteUserClick)
                    }
                ) {
                    Text(text = stringResource(R.string.delete_account))
                }
            },
            secondaryButton = {
                OutlinedButton(
                    onClick = { showDeleteAccountDialog = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FacterTopBar(
                showBackButton = false,
                title = stringResource(R.string.app_name),
                actionButton = {
                    SubcomposeAsyncImage(
                        model = state.currentUser?.photoUrl,
                        contentDescription = stringResource(R.string.user_menu),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = stringResource(R.string.user_menu)
                                )
                            }
                        },
                        contentScale = ContentScale.Fit
                    )
                },
                menuItems = listOf(
                    DropDownItem(
                        icon = Icons.AutoMirrored.Outlined.ExitToApp,
                        title = stringResource(R.string.sign_out)
                    ),
                    DropDownItem(
                        icon = Icons.Outlined.Delete,
                        title = stringResource(R.string.delete_account)
                    )
                ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> {
                            showSignOutDialog = true
                            Log.d("ListScreen", "Sign out clicked")
                        }
                        1 -> {
                            showDeleteAccountDialog = true
                            Log.d("ListScreen", "Delete account clicked")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(ListAction.OnChatClick("new")) },
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.start_new_check),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { paddingValues ->
        if (state.chatHistory.isNotEmpty()) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                items(state.chatHistory, key = { chat -> chat.id }) { chat ->
                    ChatListItem(
                        chatUi = chat,
                        onChatClicked = { onAction(ListAction.OnChatClick(chat.id)) },
                        onDeleteChat = { onAction(ListAction.OnDeleteChatClick(chat.id)) }
                    )
                }
            }
        } else { // Empty chat history
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_check_history))
            }
        }
    }
}

@Preview
@Composable
private fun ListScreenPrev() {
    val state = ListState(
        currentUser = User(
            id = "user1",
            name = "User 1",
            email = "user1@gmail.com",
            photoUrl = ""
        ),
        chatHistory = listOf(
            ChatUi(
                id = "abc",
                userId = "user1",
                title = "Do all dragons fly?",
                lastMessageTime = "08:45",
                lastMessage = "Most do, however, there are exceptions. What if a dragon is too heavy to fly?"
            ),
            ChatUi(
                id = "def",
                userId = "user1",
                title = "Are dogs descendants of wolves?",
                lastMessageTime = "08:50",
                lastMessage = "Yes, dogs as we know them are domesticated wolves"
            )
        )
    )
    FacterTheme {
        ListScreen(
            state = state,
            onAction = {}
        )
    }
}