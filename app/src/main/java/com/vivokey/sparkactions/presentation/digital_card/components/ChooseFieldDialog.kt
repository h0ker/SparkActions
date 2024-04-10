package com.vivokey.sparkactions.presentation.digital_card.components

import CustomCornersShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChooseFieldDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onWebsiteSelected: () -> Unit,
    onEmailSelected: () -> Unit,
    onPhoneNumberSelected: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Select your field type",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                onWebsiteSelected()
                                onDismiss()
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clip(CustomCornersShape(16.dp, 16.dp, 4.dp, 4.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            text = "Website"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clickable {
                                onEmailSelected()
                                onDismiss()
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clip(CustomCornersShape(4.dp, 4.dp, 4.dp, 4.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            text = "Email"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clickable {
                                onPhoneNumberSelected()
                                onDismiss()
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clip(CustomCornersShape(4.dp, 4.dp, 16.dp, 16.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            text = "Phone Number"
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}