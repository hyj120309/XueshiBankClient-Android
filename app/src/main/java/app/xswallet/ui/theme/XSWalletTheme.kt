package app.xswallet.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF5B316),
    secondary = Color(0xFF03DAC5),
    tertiary = Color(0xFFBB86FC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF5B316),
    secondary = Color(0xFF03DAC5),
    tertiary = Color(0xFFBB86FC),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF5F5F5),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun XSWalletTheme(
    darkTheme: Boolean = ThemeManager.isDarkMode,
    useDynamicColor: Boolean = ThemeManager.useDynamicColor,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dpiScale = ThemeManager.dpiScale

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            if (ThemeManager.customAccentColor != null) {
                val customColor = ThemeManager.customAccentColor!!
                darkColorScheme(
                    primary = customColor,
                    primaryContainer = customColor.copy(alpha = 0.2f),
                    onPrimary = Color.Black,
                    secondary = DarkColorScheme.secondary,
                    secondaryContainer = DarkColorScheme.secondaryContainer,
                    tertiary = DarkColorScheme.tertiary,
                    background = DarkColorScheme.background,
                    surface = DarkColorScheme.surface,
                    surfaceVariant = DarkColorScheme.surfaceVariant,
                    onBackground = DarkColorScheme.onBackground,
                    onSurface = DarkColorScheme.onSurface,
                    error = DarkColorScheme.error
                )
            } else {
                DarkColorScheme
            }
        }
        else -> {
            if (ThemeManager.customAccentColor != null) {
                val customColor = ThemeManager.customAccentColor!!
                lightColorScheme(
                    primary = customColor,
                    primaryContainer = customColor.copy(alpha = 0.1f),
                    onPrimary = Color.White,
                    secondary = LightColorScheme.secondary,
                    secondaryContainer = LightColorScheme.secondaryContainer,
                    tertiary = LightColorScheme.tertiary,
                    background = LightColorScheme.background,
                    surface = LightColorScheme.surface,
                    surfaceVariant = LightColorScheme.surfaceVariant,
                    onBackground = LightColorScheme.onBackground,
                    onSurface = LightColorScheme.onSurface,
                    error = LightColorScheme.error
                )
            } else {
                LightColorScheme
            }
        }
    }

    val density = LocalDensity.current
    val scaledDensity = remember(dpiScale, density) {
        Density(
            density = density.density * dpiScale,
            fontScale = density.fontScale
        )
    }

    CompositionLocalProvider(
        LocalDensity provides scaledDensity,
        LocalThemeManager provides ThemeManager
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}