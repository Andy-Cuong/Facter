package com.andyc.checker.presentation.check_chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.andyc.checker.presentation.R
import com.andyc.checker.presentation.check_chat.model.MessageUi
import com.andyc.core.presentation.designsystem.FacterTheme

@Composable
fun MessageListItem(
    message: MessageUi,
    modifier: Modifier = Modifier,
    userImageUrl: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (message.byUser) Arrangement.End else Arrangement.Start
    ) {
        var showTimeLabel by rememberSaveable { mutableStateOf(false) }
        val spacerWeight by animateFloatAsState(if (showTimeLabel) 0.1f else 1f)

        if (message.byUser) {
            Spacer(modifier = Modifier.weight(spacerWeight))
            AnimatedVisibility(visible = showTimeLabel) {
                Text(
                    text = message.sentAt,
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Card(
                modifier = Modifier
                    .weight(3f, fill = false)
                    .padding(horizontal = 16.dp)
                    .clickable { showTimeLabel = !showTimeLabel },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp)
                )
            }
            SubcomposeAsyncImage(
                model = userImageUrl,
                contentDescription = stringResource(R.string.user_menu),
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom),
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
        } else {
            Image(
                painter = painterResource(R.drawable.logo_facter),
                contentDescription = stringResource(R.string.from_facter),
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom),
                contentScale = ContentScale.Fit
            )
            OutlinedCard(
                modifier = Modifier
                    .weight(3f, fill = false)
                    .padding(horizontal = 16.dp)
                    .clickable { showTimeLabel = !showTimeLabel },
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp)
                )
            }
            AnimatedVisibility(visible = showTimeLabel) {
                Text(
                    text = message.sentAt,
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.weight(spacerWeight))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageListItemPrev() {
    FacterTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MessageListItem(
                message = MessageUi(
                    id = "message1",
                    byUser = true,
                    sentAt = "14:23 29/4/2025",
                    content = "Message by user. Gotta make it very long so that this can be seen " +
                            "in preview"
                )
            )
            MessageListItem(
                message = MessageUi(
                    id = "message2",
                    byUser = false,
                    sentAt = "14:24 29/4/2025",
                    content = "Message by system. Make this short and concise"
                )
            )
            MessageListItem(
                message = MessageUi(
                    id = "message3",
                    byUser = true,
                    sentAt = "15:24 29/4/2025",
                    content = "Short user message"
                )
            )
        }
    }
}