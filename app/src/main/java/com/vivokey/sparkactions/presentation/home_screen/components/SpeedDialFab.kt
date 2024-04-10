package com.vivokey.sparkactions.presentation.home_screen.components

import android.graphics.drawable.VectorDrawable
import android.provider.MediaStore.Images.ImageColumns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SpeedDialFab(
    fabText: String,
    firstOptionText: String,
    secondOptionText: String,
    firstOptionIcon: ImageVector,
    secondOptionIcon: ImageVector,
    onFirstOptionSelected: () -> Unit,
    onSecondOptionSelected: () -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }
    val currentFabText = if (isExpanded) "Close" else fabText

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                ActionButton(
                    text = firstOptionText,
                    icon = firstOptionIcon,
                    onSelected = {
                        onFirstOptionSelected()
                    }
                )
                ActionButton(
                    text = secondOptionText,
                    icon = secondOptionIcon,
                    onSelected = {
                        onSecondOptionSelected()
                    }
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                isExpanded = !isExpanded
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Clear else Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentFabText,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onSelected: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            onSelected()
        }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}