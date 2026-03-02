package app.xswallet.ui.pages.settings.screens

import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.Android16Switch
import app.xswallet.ui.pages.settings.components.ColorPickerDialog
import app.xswallet.ui.theme.ThemeManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    onBack: () -> Unit,
    strings: AppStrings
) {
    val context = LocalContext.current

    var isDarkMode by remember { mutableStateOf(ThemeManager.isDarkMode) }
    var useDynamicColor by remember { mutableStateOf(ThemeManager.useDynamicColor) }
    var accentColorHex by remember { mutableStateOf("") }
    var isWebViewVisible by remember { mutableStateOf(false) }
    var secretCodeTimer by remember { mutableStateOf(0) }
    var isSecretCodeActive by remember { mutableStateOf(false) }
    var secretCodeInputStartTime by remember { mutableStateOf<Long?>(null) }
    var showColorPicker by remember { mutableStateOf(false) }
    var isValidHexColor by remember { mutableStateOf(false) }

    var dpiScaleText by remember { mutableStateOf(ThemeManager.dpiScale.toString()) }
    var isDpiWarningVisible by remember { mutableStateOf(false) }
    var dpiWarningTimer by remember { mutableStateOf(3) }

    val isDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    LaunchedEffect(ThemeManager.isDarkMode) {
        isDarkMode = ThemeManager.isDarkMode
    }
    LaunchedEffect(ThemeManager.useDynamicColor) {
        useDynamicColor = ThemeManager.useDynamicColor
    }
    LaunchedEffect(accentColorHex) {
        isValidHexColor = try {
            accentColorHex.matches(Regex("^#[0-9A-Fa-f]{6}$"))
        } catch (e: Exception) {
            false
        }
    }

    LaunchedEffect(accentColorHex) {
        if (accentColorHex.isEmpty()) {
            secretCodeInputStartTime = null
            isSecretCodeActive = false
            secretCodeTimer = 0
            return@LaunchedEffect
        }
        if (!isSecretCodeActive && secretCodeInputStartTime == null) {
            secretCodeInputStartTime = System.currentTimeMillis()
            isSecretCodeActive = true
            val startTime = System.currentTimeMillis()
            secretCodeTimer = 20
            while (System.currentTimeMillis() - startTime < 20000) {
                val remainingTime = 20 - ((System.currentTimeMillis() - startTime) / 1000).toInt()
                secretCodeTimer = remainingTime.coerceAtLeast(0)
                delay(1000)
                if (accentColorHex == "NFJG114514BBBY" && isSecretCodeActive) {
                    isWebViewVisible = true
                    isSecretCodeActive = false
                    secretCodeInputStartTime = null
                    break
                }
            }
            isSecretCodeActive = false
            secretCodeInputStartTime = null
        }
    }

    if (isDpiWarningVisible && dpiWarningTimer > 0) {
        LaunchedEffect(dpiWarningTimer) {
            delay(1000)
            dpiWarningTimer--
            if (dpiWarningTimer <= 0) {
                isDpiWarningVisible = false
                dpiWarningTimer = 3
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = strings.back
                    )
                }
                Text(
                    text = strings.appSettingsTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        item {
            Text(
                text = strings.themeSettings,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = strings.darkMode,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = strings.darkModeDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Android16Switch(
                            checked = isDarkMode,
                            onCheckedChange = { newValue ->
                                isDarkMode = newValue
                                ThemeManager.toggleDarkMode(newValue)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = strings.dynamicColor,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            if (isDynamicColorAvailable) {
                                Text(
                                    text = strings.dynamicColorDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            } else {
                                Text(
                                    text = strings.dynamicColorUnavailable,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Android16Switch(
                                checked = useDynamicColor,
                                onCheckedChange = { newValue ->
                                    if (isDynamicColorAvailable) {
                                        useDynamicColor = newValue
                                        ThemeManager.toggleDynamicColor(newValue)
                                        if (newValue) {
                                            ThemeManager.resetAccentColor()
                                            accentColorHex = ""
                                        }
                                    }
                                },
                                enabled = isDynamicColorAvailable
                            )
                            if (!isDynamicColorAvailable) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.featureUnavailable,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = !useDynamicColor,
                enter = expandVertically(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Column {
                    Text(
                        text = strings.customThemeColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = strings.accentColorHex,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = strings.accentColorDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            OutlinedTextField(
                                value = accentColorHex,
                                onValueChange = {
                                    accentColorHex = it
                                    if (isValidHexColor) {
                                        ThemeManager.setCustomAccentColor(Color(android.graphics.Color.parseColor(it)))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text(strings.dpiExample) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                )
                            )

                            if (accentColorHex.isNotEmpty() && accentColorHex.startsWith("#") && accentColorHex.length == 7) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                try {
                                                    Color(android.graphics.Color.parseColor(accentColorHex))
                                                } catch (e: Exception) {
                                                    MaterialTheme.colorScheme.primary
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = strings.colorPreview,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = isValidHexColor,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Button(
                                            onClick = { showColorPicker = true },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = strings.confirm,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(strings.confirm)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = strings.displaySettings,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = strings.dpiScale,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = strings.dpiScaleDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = dpiScaleText,
                            onValueChange = { dpiScaleText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(strings.dpiExample) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                try {
                                    val scale = dpiScaleText.toFloat()
                                    if (scale in 0.5f..2.0f) {
                                        ThemeManager.updateDpiScale(scale)
                                        Toast.makeText(context, strings.dpiScaleDesc, Toast.LENGTH_SHORT).show()
                                    } else {
                                        isDpiWarningVisible = true
                                    }
                                } catch (e: Exception) {
                                    isDpiWarningVisible = true
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(strings.apply)
                        }
                    }

                    AnimatedVisibility(
                        visible = isDpiWarningVisible,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = strings.dpiWarning,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = strings.dpiWarning,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${strings.seconds}: ${dpiWarningTimer}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${strings.currentDpi} ${ThemeManager.dpiScale}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            colorHex = accentColorHex,
            onDismiss = {
                showColorPicker = false
                ThemeManager.resetAccentColor()
            },
            onConfirm = {
                showColorPicker = false
                try {
                    ThemeManager.setCustomAccentColor(Color(android.graphics.Color.parseColor(accentColorHex)))
                } catch (e: Exception) { }
            },
            strings = strings
        )
    }

    if (isWebViewVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { isWebViewVisible = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.secretPage,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { isWebViewVisible = false }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = strings.close
                            )
                        }
                    }
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                loadUrl("https://www.google.com")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}