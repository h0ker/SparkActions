package com.vivokey.sparkactions.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun AdditionalActionOptions(
    modifier: Modifier = Modifier,
    appendJwt: Boolean,
    onAppendJwtChange: (Boolean) -> Unit,
    delayText: String,
    onDelayChange: (Int) -> Unit
) {

    var showDropdown by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        val (title1, title2, switch, selection) = createRefs()

        Text(
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(title1) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            text = "Append JWT",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )

        Switch(
            modifier = Modifier.constrainAs(switch) {
                start.linkTo(title1.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            checked = appendJwt,
            onCheckedChange = {
                onAppendJwtChange(it)
            }
        )

        Text(
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(title2) {
                    end.linkTo(selection.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            text = "Delay",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )

        Box(
            modifier = Modifier.constrainAs(selection) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            OutlinedButton(
                onClick = {
                    showDropdown = true
                }
            ) {
                Text(
                    text = delayText,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No delay",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onDelayChange(0)
                        showDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "5 seconds",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onDelayChange(5)
                        showDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "10 seconds",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onDelayChange(10)
                        showDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "15 seconds",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onDelayChange(15)
                        showDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "30 seconds",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onDelayChange(30)
                        showDropdown = false
                    }
                )
            }
        }
    }
}