package com.amiao.voyager_bottomsheet_keyboard


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.amiao.voyager_bottomsheet_keyboard.ui.theme.VoyagerBottomSheetKeyboardTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomSheetDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
    shape: Shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
    ),
    durationMillis: Int = 400,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        val transition = updateTransition(visible, "bottom_sheet_dialog")

        val backgroundColor by transition.animateColor(
            transitionSpec = { tween(durationMillis = durationMillis, easing = LinearEasing) },
            label = "background_color",
        ) {
            if (it) {
                Color.Black.copy(alpha = 0.35f)
            } else {
                Color.Transparent
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .clickable(
                    onClick = {
                        if (canceledOnTouchOutside) {
                            onDismissRequest()
                        }
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ),
        )

        var offsetY by remember { mutableFloatStateOf(value = 0f) }
        val offsetYAnimate by animateFloatAsState(targetValue = offsetY, label = "")
        var bottomSheetHeight by remember { mutableFloatStateOf(value = 0f) }

        transition.AnimatedVisibility(
            visible = { it },
            modifier = Modifier
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                )
                .onGloballyPositioned {
                    bottomSheetHeight = it.size.height.toFloat()
                }
                .offset {
                    IntOffset(0, offsetYAnimate.roundToInt())
                }
                .draggable(
                    state = rememberDraggableState(
                        onDelta = {
                            offsetY = (offsetY + it.toInt()).coerceAtLeast(0f)
                        },
                    ),
                    orientation = Orientation.Vertical,
                    onDragStopped = {
                        if (cancelable && offsetY > bottomSheetHeight / 2) {
                            onDismissRequest()
                        } else {
                            offsetY = 0f
                        }
                    },
                ),
            enter = slideInVertically(
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = LinearOutSlowInEasing
                ),
                initialOffsetY = { 2 * it },
            ),
            exit = slideOutVertically(
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = LinearOutSlowInEasing
                ),
                targetOffsetY = { it },
            ),
        ) {
            Box(Modifier.clip(shape)) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun BottomSheetDialogPreview() {
    VoyagerBottomSheetKeyboardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
        ) {
            BottomSheetDialog(
                true,
                onDismissRequest = {},
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Content")
                    }
                }
            }
        }
    }
}