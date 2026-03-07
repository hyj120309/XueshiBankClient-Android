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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class RankItem(
    val rank: Int,
    val name: String,
    val studentId: Int,
    val total: Int
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun SharedTransitionScope.OverviewPage(
    isLoggedIn: Boolean,
    username: String,
    token: String,
    strings: AppStrings,
    isServerAvailable: Boolean,
    viewModel: OverviewViewModel,
    onRankItemClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val baseUrl = "https://bankapi.bcxs.qzz.io"
    val backgroundColor = MaterialTheme.colorScheme.background
    val scope = rememberCoroutineScope()

    val htmlContent by viewModel.htmlContent
    val isLoadingAnnouncement by viewModel.isLoading
    val announcementError by viewModel.errorMessage

    var rankList by remember { mutableStateOf<List<RankItem>>(emptyList()) }
    var isLoadingRank by remember { mutableStateOf(false) }
    var rankError by remember { mutableStateOf<String?>(null) }

    var isFullscreen by remember { mutableStateOf(false) }

    suspend fun fetchRankList(baseUrl: String): List<RankItem> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/ranklist"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                val list = mutableListOf<RankItem>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        RankItem(
                            rank = obj.getInt("RANK"),
                            name = obj.getString("name"),
                            studentId = obj.getInt("student_id"),
                            total = obj.getInt("total")
                        )
                    )
                }
                list
            } else {
                val error = connection.errorStream.bufferedReader().use { it.readText() }
                throw Exception("HTTP $responseCode: $error")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    LaunchedEffect(Unit) {
        if (isServerAvailable) {
            delay(350)
            viewModel.loadAnnouncement(baseUrl)
        }
    }

    LaunchedEffect(Unit) {
        if (isServerAvailable) {
            delay(350)
            isLoadingRank = true
            rankError = null
            try {
                rankList = fetchRankList(baseUrl)
            } catch (e: Exception) {
                rankError = e.message
            } finally {
                isLoadingRank = false
            }
        }
    }

    fun refreshAnnouncement() {
        viewModel.refresh(baseUrl)
    }

    fun refreshRank() {
        if (!isServerAvailable) return
        isLoadingRank = true
        rankError = null
        scope.launch {
            try {
                rankList = fetchRankList(baseUrl)
            } catch (e: Exception) {
                rankError = e.message
            } finally {
                isLoadingRank = false
            }
        }
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
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
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
                                enabled = isServerAvailable && htmlContent != null && !isLoadingAnnouncement
                            ) {
                                Icon(
                                    Icons.Default.ZoomIn,
                                    contentDescription = "放大"
                                )
                            }
                            IconButton(
                                onClick = { refreshAnnouncement() },
                                enabled = isServerAvailable && !isLoadingAnnouncement
                            ) {
                                if (isLoadingAnnouncement) {
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(end = 16.dp)
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
                        if (isLoadingAnnouncement) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(backgroundColor.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
                            }
                        }
                        if (announcementError != null && htmlContent == null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = announcementError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "排行榜",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.Start)
                                .padding(start = 16.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = { refreshRank() },
                            enabled = isServerAvailable && !isLoadingRank
                        ) {
                            if (isLoadingRank) {
                                MaterialExpressiveLoading(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "刷新"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                    ) {
                        if (isLoadingRank) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
                            }
                        } else if (rankError != null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "加载失败：$rankError",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else if (rankList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无排行榜数据",
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(rankList) { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onRankItemClick(item.studentId) },
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (item.rank) {
                                                1 -> MaterialTheme.colorScheme.primaryContainer
                                                2 -> MaterialTheme.colorScheme.secondaryContainer
                                                3 -> MaterialTheme.colorScheme.tertiaryContainer
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.rank}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.width(40.dp)
                                            )
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "学号：${item.studentId}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            Text(
                                                text = "${item.total}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}