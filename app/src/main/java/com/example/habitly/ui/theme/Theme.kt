package com.example.habitly.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = AppleBlue80,
    secondary = SkyBlue80,
    tertiary = SkyBlue80,
    background = Ink10,
    surface = DeepBlue20,
    surfaceVariant = DeepBlue30,
    primaryContainer = DeepBlue30,
    secondaryContainer = DeepBlue20,
    onPrimary = Ink10,
    onSecondary = Ink10,
    onTertiary = Ink10,
    onBackground = Ink80,
    onSurface = Color(0xFFEAF2FF),
    onSurfaceVariant = Color(0xFFC8D6EA),
    onPrimaryContainer = Color(0xFFEAF4FF),
    onSecondaryContainer = Color(0xFFEAF1F8)
)

private val LightColorScheme = lightColorScheme(
    primary = AppleBlue40,
    secondary = SlateBlue40,
    tertiary = AppleBlue40,
    background = NightBackground,
    surface = NightSurface,
    surfaceVariant = NightSurfaceHigh,
    primaryContainer = DeepBlue30,
    secondaryContainer = NightSurfaceHigh,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = NightText,
    onSurface = NightText,
    onSurfaceVariant = NightMutedText,
    onPrimaryContainer = Color(0xFFEAF4FF),
    onSecondaryContainer = NightText
)

private val HabitlyShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun HabitlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
        shapes = HabitlyShapes,
        content = content
    )
}
