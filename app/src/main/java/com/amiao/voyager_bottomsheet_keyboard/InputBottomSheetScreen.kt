package com.amiao.voyager_bottomsheet_keyboard

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.amiao.voyager_bottomsheet_keyboard.ui.theme.VoyagerBottomSheetKeyboardTheme

object InputBottomSheetScreen : BottomSheetScreen {
    private fun readResolve(): Any = InputBottomSheetScreen

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        InputBottomSheetContent(
            onDismissRequest = { bottomSheetNavigator.hide() },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun InputBottomSheetContent(
    onDismissRequest: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    var isCustomWidgetShown by remember { mutableStateOf(false) }
    val isImeVisible by rememberUpdatedState(WindowInsets.isImeVisible)

    LaunchedEffect(Unit) {
        snapshotFlow { isImeVisible }.collect {
            if (!it) {
                focusManager.clearFocus(false)
            }
        }
    }

    val imeHeightDp by rememberImeHeight()

    // imeHeight need soft keyboard display
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(Modifier.padding(20.dp)) {
            val inputState = rememberTextFieldState()
            Surface(
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField2(
                        inputState,
                        modifier = Modifier.focusRequester(focusRequester),
                    )
                    if (inputState.text.isEmpty()) {
                        Text(
                            "Please input",
                            color = LocalContentColor.current.copy(0.5f),
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider()
            Row {
                IconButton(onClick = {
                    if (isImeVisible) {
                        isCustomWidgetShown = true
                        focusManager.clearFocus(false)
                        softwareKeyboardController?.hide()
                    } else {
                        isCustomWidgetShown = !isCustomWidgetShown
                        if (isCustomWidgetShown) {
                            focusManager.clearFocus(false)
                            softwareKeyboardController?.hide()
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = onDismissRequest,
                ) {
                    Text("Cancel")
                }
            }
            HorizontalDivider()
            Box {
                androidx.compose.animation.AnimatedVisibility(
                    isCustomWidgetShown,
                    enter = if (isImeVisible) {
                        EnterTransition.None
                    } else {
                        slideInVertically(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            ),
                            initialOffsetY = { it * 2 },
                        ) + expandVertically(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            ),
                            initialHeight = { -it },
                        )
                    },
                    exit = slideOutVertically(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        targetOffsetY = { it * 2 },
                    ) + shrinkVertically(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        targetHeight = { -it },
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .height(imeHeightDp)
                            .fillMaxWidth(),
                    ) {
                        Spacer(Modifier.height(20.dp))
                        Text("Title")
                        Spacer(Modifier.height(20.dp))
                        Text("Content")
                    }
                }
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeContent))
            }
        }
    }
}

@Composable
private fun rememberImeHeight(): State<Dp> {
    val imeHeight = remember { mutableStateOf(200.dp) }

    val density = LocalDensity.current
    val imeBottomHeight by rememberUpdatedState(WindowInsets.ime.getBottom(density))
    LaunchedEffect(density) {
        snapshotFlow { imeBottomHeight }.collect { bottomHeight ->
            with(density) {
                imeHeight.value = maxOf(imeHeight.value.roundToPx(), bottomHeight).toDp()
            }
        }
    }
    return imeHeight
}

@Preview
@Composable
private fun InputBottomSheetContentPreview() {
    VoyagerBottomSheetKeyboardTheme {
        InputBottomSheetContent(onDismissRequest = {})
    }
}