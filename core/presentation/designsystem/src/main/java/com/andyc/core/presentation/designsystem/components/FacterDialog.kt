package com.andyc.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.andyc.core.presentation.designsystem.FacterTheme

@Composable
fun FacterDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    primaryButton: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    secondaryButton: @Composable RowScope.() -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                secondaryButton()
                Spacer(modifier = Modifier.width(8.dp))
                primaryButton()
            }
        }
    }
}

@Preview
@Composable
private fun FacterDialogPrev() {
    FacterTheme {
        FacterDialog(
            title = "Are you sure you want to delete this?",
            description = "This action cannot be undone",
            onDismissRequest = {},
            primaryButton = {
                Button(
                    onClick = {}
                ) {
                    Text("Delete")
                }
            },
            secondaryButton = {
                OutlinedButton(
                    onClick = {}
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}