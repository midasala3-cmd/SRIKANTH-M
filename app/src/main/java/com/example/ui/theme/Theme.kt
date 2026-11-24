package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = RoyalNavy,
    onPrimary = Color.White,
    primaryContainer = RoyalNavyContainer,
    onPrimaryContainer = OnRoyalNavyContainer,
    secondary = SaffronGold,
    onSecondary = Color.White,
    secondaryContainer = SaffronContainer,
    onSecondaryContainer = OnSaffronContainer,
    tertiary = EmeraldTeal,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = OnEmeraldContainer,
    background = IvoryBackground,
    onBackground = InkDark,
    surface = SurfaceWhite,
    onSurface = InkDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = InkMuted,
    outline = OutlineLight,
    outlineVariant = Color(0xFFE2E8F0),
    error = ErrorCrimson,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyLightPrimary,
    onPrimary = OnNavyLightPrimary,
    primaryContainer = NavyDarkContainer,
    onPrimaryContainer = RoyalNavyContainer,
    secondary = SaffronLight,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF5A2B04),
    onSecondaryContainer = SaffronContainer,
    tertiary = TealLight,
    onTertiary = Color(0xFF042F2E),
    tertiaryContainer = Color(0xFF115E59),
    onTertiaryContainer = EmeraldContainer,
    background = MidnightBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = MidnightSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = OutlineDark,
    outlineVariant = Color(0xFF1E293B),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

val VijetaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun VijetaApTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = VijetaShapes,
        content = content
    )
}
