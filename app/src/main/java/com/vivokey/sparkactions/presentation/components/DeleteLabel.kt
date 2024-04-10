package com.vivokey.sparkactions.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun DeleteLabel(
    offset: Float,
    color: Color
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .offset { IntOffset((offset / 4).roundToInt(), 0) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(36.dp),
            imageVector = Icons.Default.Delete,
            tint = color,
            contentDescription = null
        )
        Text(
            text = "Delete",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}