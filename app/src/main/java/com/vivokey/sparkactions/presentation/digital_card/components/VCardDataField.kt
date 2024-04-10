package com.vivokey.sparkactions.presentation.digital_card.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.presentation.components.DeleteLabel
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun VCardDataField(
    modifier: Modifier = Modifier,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit,
    onDeleteField: () -> Unit,
    keyboardType: KeyboardType,
    isSelected: Boolean,
    onFocusChange: (Boolean) -> Unit
) {

    var labelColor by remember { mutableStateOf(Color.Gray) }
    var targetOffset by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(targetOffset, label = "")
    val draggableState = rememberDraggableState { delta ->
        targetOffset += delta
        labelColor = if (abs(targetOffset) > 300) {
            Color.Red
        } else {
            Color.Gray
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeleteLabel(targetOffset, labelColor)
            DeleteLabel(targetOffset, labelColor)
        }
        Card(
            modifier = Modifier
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStopped = {
                        targetOffset = if (abs(targetOffset) > 300) {
                            onDeleteField()
                            0f
                        } else {
                            0f
                        }
                    }
                )
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .wrapContentHeight()
                .fillMaxWidth()
                .clickable {
                    onFocusChange(true)
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp),
                    imageVector = icon,
                    tint = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )

                VCardEntry(
                    modifier = Modifier.weight(1f),
                    value = value,
                    isFocused = isSelected,
                    onFocusChange = {
                        onFocusChange(it)
                    },
                    keyboardType = keyboardType,
                    onValueChange = { value ->
                        onValueChange(value)
                    }
                )
            }
        }
    }
}