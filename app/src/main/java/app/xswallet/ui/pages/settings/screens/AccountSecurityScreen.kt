package app.xswallet.ui.pages.settings.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import app.xswallet.data.SecurePrefs
import app.xswallet.ui.AppStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Composable
fun AccountSecurityScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isLoggedIn: Boolean,
    onShowLogin: () -> Unit,
    username: String,
    token: String,
    strings: AppStrings
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var userInfo by remember { mutableStateOf<Map<String, String>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchUserInfo(): Map<String, String>? = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/user/me?usrname=${URLEncoder.encode(username, "UTF-8")}&token=${URLEncoder.encode(token, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            val response = connection.inputStream.bufferedReader().use { it.readText() }.trim()
            when (responseCode) {
                200 -> {
                    if (response == "SuperAdmin") {
                        mapOf("username" to "admin", "alias" to "超级管理员", "permissions" to "")
                    } else {
                        val json = JSONObject(response)
                        val username = json.getString("username")
                        val alias = json.getString("alias")
                        // 健壮解析 permissions 字段，可能是数组或字符串
                        val permissionsStr = try {
                            val permissionsArray = json.getJSONArray("permissions")
                            (0 until permissionsArray.length()).joinToString(", ") { permissionsArray.getString(it) }
                        } catch (e: Exception) {
                            // 如果不是数组，则尝试作为字符串读取
                            json.optString("permissions", "")
                        }
                        mapOf("username" to username, "alias" to alias, "permissions" to permissionsStr)
                    }
                }
                else -> {
                    val error = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "未知错误"
                    throw Exception("HTTP $responseCode: $error")
                }
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    LaunchedEffect(Unit) {
        if (isLoggedIn) {
            isLoading = true
            errorMessage = null
            try {
                val info = fetchUserInfo()
                userInfo = info
            } catch (e: Exception) {
                errorMessage = e.message
                Toast.makeText(context, "获取用户信息失败: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                text = "账号设置",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoggedIn) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else if (userInfo != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "用户名：${userInfo!!["username"]}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "别名：${userInfo!!["alias"]}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        if (userInfo!!["permissions"]!!.isNotEmpty()) {
                            Text(
                                text = "权限组：${userInfo!!["permissions"]}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else if (errorMessage != null) {
                Text(
                    text = "加载失败，请稍后重试",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = {
                    SecurePrefs.clearUser(context)
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("退出登录")
            }
        } else {
            Button(
                onClick = onShowLogin,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("去登录")
            }
        }
    }
}