package com.vivokey.sparkactions.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun GradientLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    topGradientHeight: Float = 40f,
    bottomGradientHeight: Float = 40f,
    topGradientColor: Color = MaterialTheme.colorScheme.background,
    bottomGradientColor: Color = MaterialTheme.colorScheme.background,
    content: LazyListScope.() -> Unit
) {

    val density = LocalDensity.current
    val topGradientHeightPx = with(density) { topGradientHeight.dp.toPx() }
    val bottomGradientHeightPx = with(density) { bottomGradientHeight.dp.toPx() }

    val showTopGradient by remember {
        derivedStateOf { state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0 }
    }

    val showBottomGradient by remember(state) {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) {
                false // No items are visible
            } else {
                val lastVisibleItemInfo = layoutInfo.visibleItemsInfo.last()
                val lastItemBottomEdge = lastVisibleItemInfo.offset + lastVisibleItemInfo.size
                lastItemBottomEdge > layoutInfo.viewportEndOffset
            }
        }
    }

    Box(modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            content = content
        )

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .height(topGradientHeight.dp),
            visible = showTopGradient,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topGradientHeight.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(topGradientColor, Color.Transparent),
                            startY = 0f,
                            endY = topGradientHeightPx
                        )
                    )
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(bottomGradientHeight.dp),
            visible = showBottomGradient,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomGradientHeight.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(bottomGradientColor, Color.Transparent),
                            startY = bottomGradientHeightPx,
                            endY = 0f
                        )
                    )
            )
        }
    }
}