package com.vivokey.sparkactions.presentation.digital_card.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.presentation.digital_card.DigitalCardViewModel
import java.lang.Float.max
import java.lang.Float.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropper(
    viewModel: DigitalCardViewModel,
    popBack: () -> Unit
) {

    var zoom by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current
    val oneHundredDpInPx = with(density) { 100.dp.toPx() }

    var topLeft by remember { mutableStateOf(Offset(0f, 0f)) }
    var bottomRight by remember { mutableStateOf(Offset(0f, 0f)) }

    var containerSize by remember { mutableStateOf(IntSize(0, 0)) }
    var offset by remember { mutableStateOf(Offset(containerSize.width / 2f, containerSize.width / 2f)) }

    var displayWidth by remember { mutableFloatStateOf(0f) }
    var displayHeight by remember { mutableFloatStateOf(0f) }

    var paddingX by remember { mutableFloatStateOf(0f) }
    var paddingY by remember { mutableFloatStateOf(0f) }

    Scaffold(
        floatingActionButton = {

            FloatingActionButton(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    if (viewModel.cropImage(
                            topLeft,
                            bottomRight,
                            displayWidth,
                            displayHeight,
                            paddingX,
                            paddingY
                        )) {
                        popBack()
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Changes",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { paddingValues ->

        LaunchedEffect(Unit) {

            val smallerSide = min(displayWidth, displayHeight)
            val largerSide = max(displayWidth, displayHeight)

            val xIsSmaller = displayWidth <= displayHeight

            val newSmallerSideTopLeft = smallerSide * .125f + (if (xIsSmaller) paddingX else paddingY)
            val newSmallerSideBottomRight = smallerSide * .875f + (if (xIsSmaller) paddingX else paddingY)

            val reticuleWidth = newSmallerSideBottomRight - newSmallerSideTopLeft

            val newLargerSideTopLeft = (largerSide / 2f) - (reticuleWidth / 2f) + (if (xIsSmaller) paddingY else paddingX)
            val newLargerSideBottomRight = (largerSide / 2f) + (reticuleWidth / 2f) + (if (xIsSmaller) paddingY else paddingX)

            zoom = reticuleWidth / oneHundredDpInPx
            topLeft = if (xIsSmaller) Offset(newSmallerSideTopLeft, newLargerSideTopLeft) else Offset(newLargerSideTopLeft, newSmallerSideTopLeft)
            bottomRight = if (xIsSmaller) Offset(newSmallerSideBottomRight, newLargerSideBottomRight) else Offset(newLargerSideBottomRight, newSmallerSideBottomRight)
            offset = Offset(topLeft.x, topLeft.y)

            Log.d("TopLeft:", topLeft.toString())
            Log.d("BottomRight:", bottomRight.toString())
            Log.d("Zoom:", zoom.toString())
        }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    containerSize = coordinates.size
                    val result = viewModel.calculateDisplayDimensions(
                        containerWidth = containerSize.width.toFloat(),
                        containerHeight = containerSize.height.toFloat()
                    )
                    displayWidth = result.first
                    displayHeight = result.second

                    paddingX = (containerSize.width - displayWidth) / 2
                    paddingY = (containerSize.height - displayHeight) / 2
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChanged, _ ->
                        val newZoom = zoom * zoomChanged
                        val newOffsetX = offset.x + pan.x
                        val newOffsetY = offset.y + pan.y

                        val effectiveTopLeftX = max(paddingX, newOffsetX)
                        val effectiveTopLeftY = max(paddingY, newOffsetY)

                        val effectiveBottomRightX = min(
                            containerSize.width - paddingX,
                            newOffsetX + oneHundredDpInPx * newZoom
                        )
                        val effectiveBottomRightY = min(
                            containerSize.height - paddingY,
                            newOffsetY + oneHundredDpInPx * newZoom
                        )

                        if (displayWidth >= oneHundredDpInPx * newZoom && displayHeight >= oneHundredDpInPx * newZoom) {
                            val constrainedTopLeftX = effectiveTopLeftX.coerceIn(
                                paddingX,
                                paddingX + displayWidth - oneHundredDpInPx * newZoom
                            )
                            val constrainedTopLeftY = effectiveTopLeftY.coerceIn(
                                paddingY,
                                paddingY + displayHeight - oneHundredDpInPx * newZoom
                            )
                            val constrainedBottomRightX = effectiveBottomRightX.coerceIn(
                                paddingX + oneHundredDpInPx * newZoom,
                                paddingX + displayWidth
                            )
                            val constrainedBottomRightY = effectiveBottomRightY.coerceIn(
                                paddingY + oneHundredDpInPx * newZoom,
                                paddingY + displayHeight
                            )

                            topLeft = Offset(constrainedTopLeftX, constrainedTopLeftY)
                            bottomRight = Offset(constrainedBottomRightX, constrainedBottomRightY)
                            zoom = newZoom
                            offset = Offset(constrainedTopLeftX, constrainedTopLeftY)
                        }
                        Log.d("DisplayWidth:", displayWidth.toString())
                        Log.d("DisplayHeight:", displayHeight.toString())
                        Log.d("TopLeft:", topLeft.toString())
                        Log.d("BottomRight:", bottomRight.toString())
                        Log.d("Zoom:", zoom.toString())
                    }
                }
        ) {
            viewModel.digitalCard.bitmap?.asImageBitmap()?.let { bitmap ->
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

            Reticule(
                modifier = Modifier.matchParentSize(),
                topLeft = topLeft,
                bottomRight = bottomRight,
                density = density
            )
        }
    }
}

@Composable
fun Reticule(
    modifier: Modifier = Modifier,
    topLeft: Offset,
    bottomRight: Offset,
    density: Density
) {
    Box(
        modifier = modifier
    ) {

        Box(
            modifier = Modifier
                .graphicsLayer(
                    translationX = topLeft.x,
                    translationY = topLeft.y
                )
                .size((bottomRight.x / density.density - topLeft.x / density.density).dp)
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawIntoCanvas { canvas ->

                    val paint = Paint().apply {
                        color = Color.Gray.copy(alpha = .5f)
                    }

                    val squareRect = Rect(0f, 0f, size.width, size.height)
                    val squarePath = Path().apply {
                        addRect(squareRect)
                    }

                    val circlePath = Path().apply {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2
                        addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
                    }

                    val resultPath = Path().apply {
                        op(squarePath, circlePath, PathOperation.Difference)
                    }

                    canvas.drawPath(resultPath, paint)
                }
            }

            LShapedCorner(
                modifier = Modifier.align(Alignment.TopStart),
                color = Color.White,
                thickness = 1.dp,
                length = 16.dp
            )
            LShapedCorner(
                modifier = Modifier
                    .rotate(90f)
                    .align(Alignment.TopEnd),
                color = Color.White,
                thickness = 1.dp,
                length = 16.dp
            )
            LShapedCorner(
                modifier = Modifier
                    .rotate(-90f)
                    .align(Alignment.BottomStart),
                color = Color.White,
                thickness = 1.dp,
                length = 16.dp
            )
            LShapedCorner(
                modifier = Modifier
                    .rotate(180f)
                    .align(Alignment.BottomEnd),
                color = Color.White,
                thickness = 1.dp,
                length = 16.dp
            )
        }
    }
}

@Composable
fun LShapedCorner(
    modifier: Modifier = Modifier,
    color: Color,
    thickness: Dp,
    length: Dp
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(color)
                .size(thickness, length)
        )
        Box(
            modifier = Modifier
                .background(color)
                .size(length, thickness)
        )
    }
}