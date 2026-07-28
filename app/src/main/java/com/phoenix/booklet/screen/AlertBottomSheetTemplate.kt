package com.phoenix.booklet.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlertBottomSheetTemplate(
    title: String,
    message: String,
    dismissText: String,
    confirmText: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ),
    confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(),
    isDismissAllowed: Boolean = true
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 16.sp,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(isDismissAllowed) {
                Button(
                    onClick = { onDismiss() },
                    colors = dismissButtonColors,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(dismissText)
                }
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onConfirm() },
                colors = confirmButtonColors,
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                AnimatedVisibility(isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                }
                AnimatedVisibility(!isLoading) {
                    Text(confirmText)
                }

            }
        }
    }
}