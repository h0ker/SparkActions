package com.vivokey.sparkactions.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashScreenEnd: () -> Unit) {

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(1000),
        label = ""
    )

    AnimatedVisibility(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .graphicsLayer { alpha = alphaAnimation.value },
        visible = alphaAnimation.value > 0f
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_screen),
                contentScale = ContentScale.FillBounds,
                contentDescription = null
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxWidth(.75f),
                    painter = painterResource(id = R.drawable.vivokey),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(.5f),
                    painter = painterResource(id = R.drawable.spark_icon),
                    contentDescription = null
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        startAnimation = true
        delay(1000)
        onSplashScreenEnd()
    }
}