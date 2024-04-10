package com.vivokey.sparkactions.presentation.home_screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun HomeScreenTopBar() {
    Row {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Spark Actions",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp
        )
    }
}