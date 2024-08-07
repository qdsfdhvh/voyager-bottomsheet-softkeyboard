package com.amiao.voyager_bottomsheet_keyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.navigator.Navigator
import com.amiao.voyager_bottomsheet_keyboard.ui.theme.VoyagerBottomSheetKeyboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoyagerBottomSheetKeyboardTheme {
                Navigator(HomeScreen) {
                    BottomSheetContent()
                }
            }
        }
    }
}
