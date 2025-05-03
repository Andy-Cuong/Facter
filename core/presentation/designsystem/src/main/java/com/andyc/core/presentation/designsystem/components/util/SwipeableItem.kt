package com.andyc.core.presentation.designsystem.components.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.andyc.core.presentation.designsystem.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> SwipeableItem(
    item: T,
    onDelete: (T) -> Unit,
    isRevealed: Boolean,
    deleteConfirmed: Boolean,
    modifier: Modifier = Modifier,
    deleteAnimationDuration: Int = 500,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    var contextMenuWidth by remember { mutableFloatStateOf(0f) }
    var isDeleted by remember { mutableStateOf(false) }
    val currentOnExpanded by rememberUpdatedState(onExpanded)
    val currentOnCollapsed by rememberUpdatedState(onCollapsed)
    val offset = remember { Animatable(initialValue = 0f) }
    val coroutineScope = rememberCoroutineScope()

    // Used to programmatically control the swipe state if needed
    LaunchedEffect(isRevealed, contextMenuWidth, isDeleted, deleteConfirmed) {
        if (isRevealed) {
            offset.animateTo(-contextMenuWidth)
            currentOnExpanded()
        } else {
            offset.animateTo(0f)
            currentOnCollapsed()
        }

        if (isDeleted) {
//            delay(deleteAnimationDuration.toLong())
            onDelete(item)
            isDeleted = false
        }
    }

    AnimatedVisibility( // Animate the removal action
        visible = !deleteConfirmed,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = deleteAnimationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Row( // The row for the contextMenu
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .onSizeChanged {
                        // Measure the width of the context menu on drawn
                        contextMenuWidth = it.width.toFloat()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            offset.animateTo(0f)
                        }
                        onCollapsed()
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.LightGray)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.cancel),
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            offset.animateTo(0f)
                        }
                        isDeleted = true
                        onCollapsed()
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.Red)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = Color.White
                    )
                }
            }

            Surface( // The content overlapping the context menu
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = offset.value.roundToInt(),
                            y = 0
                        )
                    } // Use negative since we want to drag from end to start
                    .pointerInput(key1 = contextMenuWidth) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                coroutineScope.launch {
                                    val newOffset = (offset.value + dragAmount)
                                        .coerceIn( // Used to force swipe from end to start only
                                            minimumValue = -contextMenuWidth,
                                            maximumValue = 0f
                                        )
                                    offset.snapTo(newOffset)
                                }
                            },
                            onDragEnd = {
                                when {
                                    offset.value <= contextMenuWidth / -2f -> {
                                        coroutineScope.launch {
                                            offset.animateTo(-contextMenuWidth)
                                            onExpanded()
                                        }
                                    }

                                    else -> {
                                        coroutineScope.launch {
                                            offset.animateTo(0f)
                                            onCollapsed()
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                content(item)
            }
        }
    }
}