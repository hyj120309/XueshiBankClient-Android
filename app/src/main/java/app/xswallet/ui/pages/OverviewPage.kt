package app.xswallet.ui.pages

import android.graphics.Color as AndroidColor
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.OverviewPage(
    isLoggedIn: Boolean,
    username: String,
    token: String,
    strings: AppStrings,
    viewModel: OverviewViewModel
) {
    val context = LocalContext.current
    val baseUrl = "https://bankapi.bcxs.qzz.io"
    val backgroundColor = MaterialTheme.colorScheme.background

    val htmlContent by viewModel.htmlContent
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.loadAnnouncement(baseUrl)
    }

    fun refresh() {
        viewModel.refresh(baseUrl)
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "公告",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { refresh() },
                    enabled = !isLoading
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

        item {
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
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(backgroundColor)
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
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor),
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

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}