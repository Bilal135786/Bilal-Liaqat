package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// High contrast input text style when typing / filling data
val MaveshiInputTextStyle = TextStyle(
    color = Color(0xFF0F172A),
    fontSize = 15.sp,
    fontWeight = FontWeight.Normal
)

// Standard high contrast text field color styling across the app
@Composable
fun maveshiTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF1E293B),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = MaweshiGreenPrimary,
    focusedBorderColor = MaweshiGreenPrimary,
    unfocusedBorderColor = Color(0xFFCBD5E1),
    focusedLabelColor = MaweshiGreenPrimary,
    unfocusedLabelColor = Color(0xFF64748B),
    focusedPlaceholderColor = Color(0xFF94A3B8),
    unfocusedPlaceholderColor = Color(0xFF94A3B8)
)

private val DarkColorScheme = darkColorScheme(
    primary = MandiOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = MandiOrangeContainer,
    onPrimaryContainer = MandiOnOrangeContainer,
    secondary = MandiAmber,
    onSecondary = Color.Black,
    background = MandiBackground,
    surface = MandiSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = MandiTextPrimary,
    onSurface = MandiTextPrimary,
    outline = MandiCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = MandiOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = MandiOrangeContainer,
    onPrimaryContainer = MandiOnOrangeContainer,
    secondary = MandiAmber,
    onSecondary = Color.Black,
    background = MandiBackground,
    surface = MandiSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = MandiTextPrimary,
    onSurface = MandiTextPrimary,
    outline = MandiCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature branded look
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
