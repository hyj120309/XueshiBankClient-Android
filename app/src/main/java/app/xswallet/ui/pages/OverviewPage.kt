package app.xswallet.ui.pages

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.OverviewPage(
    isLoggedIn: Boolean,
    username: String,
    token: String,
    strings: AppStrings
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var announcement by remember { mutableStateOf("加载中...") }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchAnnouncement(): String = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/getpub"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
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
        isLoading = true
        errorMessage = null
        try {
            val result = fetchAnnouncement()
            announcement = result
        } catch (e: Exception) {
            errorMessage = "获取公告失败：${e.message}"
            announcement = ""
        } finally {
            isLoading = false
        }
    }

    fun refresh() {
        scope.launch {
            isRefreshing = true
            errorMessage = null
            try {
                val result = fetchAnnouncement()
                announcement = result
            } catch (e: Exception) {
                errorMessage = "获取公告失败：${e.message}"
                announcement = ""
            } finally {
                isRefreshing = false
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
                    enabled = !isRefreshing && !isLoading
                ) {
                    if (isRefreshing) {
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
                        }
                    } else if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Markdown(
                            content = announcement,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}