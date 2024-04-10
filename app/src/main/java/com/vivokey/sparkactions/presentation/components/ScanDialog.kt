package com.vivokey.sparkactions.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ScanDialog(
    isVisible: Boolean,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (isLoading) "Hold still" else "Scan your Spark",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Default.Nfc,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {},
            properties = DialogProperties(dismissOnBackPress = !isLoading, dismissOnClickOutside = !isLoading)
        )
    }
}