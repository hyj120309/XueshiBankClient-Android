package app.xswallet.ui.pages

import android.graphics.Color as AndroidColor
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun SharedTransitionScope.OverviewPage(
    isLoggedIn: Boolean,
    username: String,
    token: String,
    strings: AppStrings,
    isServerAvailable: Boolean,
    viewModel: OverviewViewModel
) {
    val context = LocalContext.current
    val baseUrl = "https://bankapi.bcxs.qzz.io"
    val backgroundColor = MaterialTheme.colorScheme.background

    val htmlContent by viewModel.htmlContent
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var isFullscreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isServerAvailable) {
            viewModel.loadAnnouncement(baseUrl)
        }
    }

    fun refresh() {
        viewModel.refresh(baseUrl)
    }

    val fullscreenTransition = updateTransition(targetState = isFullscreen, label = "fullscreen")
    val scale by fullscreenTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 400, easing = FastOutSlowInEasing) },
        label = "scale"
    ) { fullscreen ->
        if (fullscreen) 1f else 0.9f
    }
    val alpha by fullscreenTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 400, easing = FastOutSlowInEasing) },
        label = "alpha"
    ) { fullscreen ->
        if (fullscreen) 1f else 0f
    }

    BackHandler(enabled = isFullscreen) {
        isFullscreen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isFullscreen) Color.Black else Color.Transparent)
    ) {
        if (isFullscreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
            ) {
                htmlContent?.let { content ->
                    val webView = remember {
                        WebView(context).apply {
                            setBackgroundColor(AndroidColor.TRANSPARENT)
                            settings.javaScriptEnabled = true
                            webViewClient = WebViewClient()
                        }
                    }
                    LaunchedEffect(content) {
                        webView.loadDataWithBaseURL(baseUrl, content, "text/html", "UTF-8", null)
                    }
                    AndroidView(
                        factory = { webView },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { isFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "缩小",
                        tint = Color.White
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "公告",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row {
                        IconButton(
                            onClick = { isFullscreen = true },
                            enabled = isServerAvailable && htmlContent != null && !isLoading
                        ) {
                            Icon(
                                Icons.Default.ZoomIn,
                                contentDescription = "放大"
                            )
                        }
                        IconButton(
                            onClick = { refresh() },
                            enabled = isServerAvailable && !isLoading
                        ) {
                            if (isLoading) {
                                MaterialExpressiveLoading(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "刷新"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = if (isLoggedIn) "当前用户：$username" else "未登录",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    htmlContent?.let { content ->
                        val webView = remember {
                            WebView(context).apply {
                                setBackgroundColor(AndroidColor.TRANSPARENT)
                                settings.javaScriptEnabled = true
                                webViewClient = WebViewClient()
                            }
                        }
                        LaunchedEffect(content) {
                            webView.loadDataWithBaseURL(baseUrl, content, "text/html", "UTF-8", null)
                        }
                        AndroidView(
                            factory = { webView },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundColor.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
                        }
                    }
                    if (errorMessage != null && htmlContent == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}