package com.redoy.volumecontroll.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.designsystem.theme.VolumeControllTheme
import kotlin.math.abs

@Composable
fun FloatingVolumeButton(
    onClick: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit = {},
    onVerticalScroll: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    opacity: Float = 0.9f,
    shape: String = "CIRCLE",
    colorStyle: String = "PRIMARY"
) {
    val haptic = LocalHapticFeedback.current
    val buttonShape = when (shape) {
        "ROUNDED_SQUARE" -> RoundedCornerShape(12.dp)
        "PILL" -> RoundedCornerShape(50)
        else -> CircleShape
    }
    val buttonColor = when (colorStyle) {
        "SECONDARY" -> MaterialTheme.colorScheme.secondaryContainer
        "TERTIARY" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(buttonShape)
            .background(buttonColor.copy(alpha = opacity))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    }
                ) { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                    if (abs(dragAmount.y) > abs(dragAmount.x)) {
                        onVerticalScroll(dragAmount.y)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Floating Volume Controller",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingVolumeButtonPreview() {
    VolumeControllTheme {
        FloatingVolumeButton(
            onClick = {},
            onDrag = {},
            onDragEnd = {},
            onVerticalScroll = {}
        )
    }
}
