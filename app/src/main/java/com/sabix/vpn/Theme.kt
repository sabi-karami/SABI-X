package com.sabix.vpn

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SABI-X brand palette.
 * a = neon cyan, b = electric violet, c = hot magenta, busy = amber (connecting / reconnecting).
 */
@Immutable
class Extra(val a: Color, val b: Color, val busy: Color, val c: Color = b) {
    val gradient: Brush get() = Brush.linearGradient(listOf(a, b))
    val tri: Brush get() = Brush.linearGradient(listOf(a, b, c))
    val hot: Brush get() = Brush.horizontalGradient(listOf(b, c))
}

private val NeonDark = Extra(Color(0xFF00E5FF), Color(0xFF8B5CF6), Color(0xFFFFB020), Color(0xFFFF2E97))
private val NeonLight = Extra(Color(0xFF06B6D4), Color(0xFF7C3AED), Color(0xFFD97706), Color(0xFFDB2777))

val LocalExtra = staticCompositionLocalOf { NeonDark }

private val Dark = darkColorScheme(
    primary = Color(0xFFA78BFA), onPrimary = Color(0xFF14002E),
    primaryContainer = Color(0xFF3B1F7A), onPrimaryContainer = Color(0xFFEDE4FF),
    secondary = Color(0xFF00E5FF), onSecondary = Color(0xFF00313A),
    tertiary = Color(0xFFFF2E97), onTertiary = Color(0xFF3D0020),
    background = Color(0xFF06050B), onBackground = Color(0xFFEDEBFA),
    surface = Color(0xFF06050B), onSurface = Color(0xFFEDEBFA),
    surfaceVariant = Color(0xFF221D3A), onSurfaceVariant = Color(0xFFA6A1C4),
    surfaceContainer = Color(0xFF0E0C19), surfaceContainerHigh = Color(0xFF161328),
    surfaceContainerHighest = Color(0xFF1F1A36),
    outline = Color(0xFF4C4572), outlineVariant = Color(0xFF241F3E),
    error = Color(0xFFFF6B81),
)

private val Light = lightColorScheme(
    primary = Color(0xFF6D28D9), onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE4FF), onPrimaryContainer = Color(0xFF24005C),
    secondary = Color(0xFF0891B2), onSecondary = Color.White,
    tertiary = Color(0xFFDB2777), onTertiary = Color.White,
    background = Color(0xFFF6F4FC), onBackground = Color(0xFF15122A),
    surface = Color(0xFFF6F4FC), onSurface = Color(0xFF15122A),
    surfaceVariant = Color(0xFFE7E3F5), onSurfaceVariant = Color(0xFF5A5478),
    surfaceContainer = Color(0xFFFFFFFF), surfaceContainerHigh = Color(0xFFF0EDFA),
    surfaceContainerHighest = Color(0xFFE8E4F7),
    outline = Color(0xFF8F88AE), outlineVariant = Color(0xFFDDD8EE),
    error = Color(0xFFD9304A),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

private val Base = Typography()
private val AppType = Base.copy(
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.5.sp),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp),
    titleSmall = Base.titleSmall.copy(letterSpacing = 0.4.sp),
    labelSmall = Base.labelSmall.copy(letterSpacing = 0.8.sp),
)

@Composable
fun AppTheme(s: Settings, dark: Boolean, content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val dynamic = s.dynamicColor && Build.VERSION.SDK_INT >= 31
    val scheme = when {
        dynamic -> if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        dark -> Dark
        else -> Light
    }
    val extra = when {
        dynamic -> Extra(scheme.secondary, scheme.primary, if (dark) NeonDark.busy else NeonLight.busy, scheme.tertiary)
        dark -> NeonDark
        else -> NeonLight
    }
    CompositionLocalProvider(LocalExtra provides extra) {
        MaterialTheme(colorScheme = scheme, shapes = AppShapes, typography = AppType, content = content)
    }
}

@Composable
fun isDark(s: Settings): Boolean = when (s.theme) {
    "dark" -> true
    "light" -> false
    else -> isSystemInDarkTheme()
}
