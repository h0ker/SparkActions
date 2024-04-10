package com.vivokey.sparkactions.presentation.home_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.DigitalCardActionTarget
import com.vivokey.sparkactions.presentation.components.DeleteLabel
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    action: Action,
    onDeleteAction: (() -> Unit)? = null,
    onActionSelected: (() -> Unit)? = null
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    enabled = onDeleteAction != null,
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStopped = {
                        targetOffset = if (abs(targetOffset) > 300) {
                            onDeleteAction?.invoke()
                            0f
                        } else {
                            0f
                        }
                    },
                )
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .clickable(enabled = onActionSelected != null) {
                    onActionSelected?.invoke()
                }
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                action.getIcon()?.let { icon ->
                    (icon as? Bitmap)?.asImageBitmap()?.let { bitmapIcon ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(60.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Image(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .size(56.dp),
                                bitmap = bitmapIcon,
                                contentDescription = null,
                            )
                        }
                    }
                }

                action.getIcon()?.let { icon ->
                    (icon as? ImageVector)?.let { vectorIcon ->
                        Icon(
                            imageVector = vectorIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (action.getIcon() == null) {
                    Image(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = action.title,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    )
                    if (action.target !is DigitalCardActionTarget) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = action.target.describeContents(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                onActionSelected?.let {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}