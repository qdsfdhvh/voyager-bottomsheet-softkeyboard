package com.amiao.voyager_bottomsheet_keyboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF3A64C),
    onPrimary = Color.White,
    secondary = Color(0xFFFFC85C),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFDD99),
    onTertiary = Color.Black,
    background = Color(0xFFFFF8EB),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color(0xFF333333),
    surfaceContainer = Color(0xFFE6E6E6),
    surfaceBright = Color(0xFFF2F2F2),
    surfaceVariant = Color.White,
    onSurfaceVariant = Color(0xFF999999),
    outlineVariant = Color(0xFFF1F1F1),
    error = Color(0xFFFF5D5D),
)

@Composable
fun VoyagerBottomSheetKeyboardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}