package com.amiao.voyager_bottomsheet_keyboard

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent() {
    val navigator = LocalNavigator.currentOrThrow

    // Screen

    val lastFullScreen by remember {
        derivedStateOf {
            navigator.items.last { it !is BottomSheetScreen }
        }
    }

    navigator.saveableState("currentScreen") {
        lastFullScreen.Content()
    }

    // BottomSheet

    val scope = rememberCoroutineScope()

    val lastBottomSheetScreen by remember {
        derivedStateOf {
            navigator.items.lastOrNull { it is BottomSheetScreen }
        }
    }

    lastBottomSheetScreen?.let { screen ->
        val focusManager = LocalFocusManager.current
        val softwareKeyboardController = LocalSoftwareKeyboardController.current

        val bottomSheetNavigator = remember {
            BottomSheetNavigator(
                navigator = navigator,
                focusManager = focusManager,
                softwareKeyboardController = softwareKeyboardController,
                scope = scope,
            )
        }

        CompositionLocalProvider(
            LocalBottomSheetNavigator provides bottomSheetNavigator
        ) {
            BottomSheetDialog(
                visible = bottomSheetNavigator.isShow,
                onDismissRequest = {
                    bottomSheetNavigator.hide()
                },
                durationMillis = 400,
            ) {
                navigator.saveableState("bottomSheet", screen) {
                    BackHandler {
                        bottomSheetNavigator.hide()
                    }
                    screen.Content()
                }
            }
            LaunchedEffect(screen) {
                bottomSheetNavigator.show()
            }
        }
    }
}

val LocalBottomSheetNavigator = staticCompositionLocalOf<BottomSheetNavigator> { error("Not provide BottomSheetNavigator") }

class BottomSheetNavigator(
    private val navigator: Navigator,
    private val focusManager: FocusManager,
    private val softwareKeyboardController: SoftwareKeyboardController?,
    private val scope: CoroutineScope,
) {

    private var isBottomSheetShown by mutableStateOf(false)

    val isShow get() = isBottomSheetShown

    fun show() {
        if (!isBottomSheetShown) {
            isBottomSheetShown = true
        }
    }

    fun hide() {
        if (isBottomSheetShown) {
            scope.launch {
                focusManager.clearFocus(false)
                softwareKeyboardController?.hide()

                isBottomSheetShown = false
                delay(400)
                navigator.pop()
            }
        }
    }
}