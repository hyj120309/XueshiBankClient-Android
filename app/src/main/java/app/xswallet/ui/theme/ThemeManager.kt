package app.xswallet.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import androidx.core.graphics.toColorInt

object ThemeManager {
    private const val PREFS_NAME = "xswallet_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_USE_DYNAMIC_COLOR = "use_dynamic_color"
    private const val KEY_CUSTOM_COLOR = "custom_color"
    private const val KEY_DPI_SCALE = "dpi_scale"

    private lateinit var prefs: SharedPreferences

    var isDarkMode by mutableStateOf(true)
    var useDynamicColor by mutableStateOf(false)
    var customAccentColor by mutableStateOf<Color?>(null)
    var dpiScale by mutableStateOf(1.0f)
        private set

    val isDynamicColorAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadSettings()
        if (isDynamicColorAvailable && !prefs.contains(KEY_USE_DYNAMIC_COLOR)) {
            useDynamicColor = true
            saveSettings()
        }
    }

    private fun loadSettings() {
        isDarkMode = prefs.getBoolean(KEY_DARK_MODE, true)
        useDynamicColor = prefs.getBoolean(KEY_USE_DYNAMIC_COLOR, false)
        dpiScale = prefs.getFloat(KEY_DPI_SCALE, 1.0f)

        val colorHex = prefs.getString(KEY_CUSTOM_COLOR, null)
        if (!colorHex.isNullOrEmpty()) {
            try {
                customAccentColor = Color(android.graphics.Color.parseColor(colorHex))
            } catch (e: Exception) {
            }
        }
    }

    private fun saveSettings() {
        prefs.edit {
            putBoolean(KEY_DARK_MODE, isDarkMode)
            putBoolean(KEY_USE_DYNAMIC_COLOR, useDynamicColor)
            putFloat(KEY_DPI_SCALE, dpiScale)

            if (customAccentColor != null) {
                val colorInt = customAccentColor!!.toArgb()
                putString(KEY_CUSTOM_COLOR, String.format("#%08X", colorInt))
            } else {
                remove(KEY_CUSTOM_COLOR)
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        isDarkMode = enabled
        saveSettings()
    }

    fun toggleDynamicColor(enabled: Boolean) {
        if (isDynamicColorAvailable) {
            useDynamicColor = enabled
        } else {
            useDynamicColor = false
        }
        saveSettings()
    }

    fun setCustomAccentColor(color: Color) {
        customAccentColor = color
        saveSettings()
    }

    fun resetAccentColor() {
        customAccentColor = null
        saveSettings()
    }

    fun updateDpiScale(scale: Float) {
        dpiScale = scale
        saveSettings()
    }

    val currentAccentColor: Color
        get() = customAccentColor ?: Color(0xFF66CCFF)
}

val LocalThemeManager = staticCompositionLocalOf { ThemeManager }