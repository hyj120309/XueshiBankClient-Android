package app.xswallet.ui.pages.settings.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import app.xswallet.data.SecurePrefs
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
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
    var loadedUsername by remember { mutableStateOf("") }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isChangingPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordStrength by remember { mutableStateOf<String?>(null) }

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
                        val permissionsField = json.get("permissions")
                        val permissionsStr = when (permissionsField) {
                            is JSONArray -> {
                                (0 until permissionsField.length()).joinToString(", ") { permissionsField.getString(it) }
                            }
                            is String -> {
                                permissionsField
                            }
                            else -> ""
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

    suspend fun changePassword(newPass: String): Boolean = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/user/setpw?usrname=${URLEncoder.encode(username, "UTF-8")}&token=${URLEncoder.encode(token, "UTF-8")}&passwd=${URLEncoder.encode(newPass, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            val response = connection.inputStream.bufferedReader().use { it.readText() }.trim()
            responseCode == 200
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    fun checkPasswordStrength(pass: String): String {
        return when {
            pass.length < 6 -> "密码太短"
            pass.matches(Regex("[0-9]+")) -> "弱"
            pass.matches(Regex("[a-zA-Z]+")) -> "中"
            pass.matches(Regex(".*[0-9].*")) && pass.matches(Regex(".*[a-zA-Z].*")) && pass.length >= 8 -> "强"
            else -> "中"
        }
    }

    LaunchedEffect(isLoggedIn, username, token) {
        if (isLoggedIn && (loadedUsername != username || userInfo == null)) {
            isLoading = true
            errorMessage = null
            try {
                val info = fetchUserInfo()
                userInfo = info
                loadedUsername = username
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
                    MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
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

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "修改密码",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("原密码") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = !isChangingPassword
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                passwordStrength = checkPasswordStrength(it)
                            },
                            label = { Text("新密码") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = !isChangingPassword,
                            isError = passwordError != null && newPassword.isNotBlank()
                        )
                        if (passwordStrength != null && newPassword.isNotBlank()) {
                            Text(
                                text = "密码强度：$passwordStrength",
                                style = MaterialTheme.typography.bodySmall,
                                color = when (passwordStrength) {
                                    "强" -> MaterialTheme.colorScheme.primary
                                    "中" -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("确认新密码") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            enabled = !isChangingPassword,
                            isError = passwordError != null
                        )

                        if (passwordError != null) {
                            Text(
                                text = passwordError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (newPassword != confirmPassword) {
                                    passwordError = "两次输入的密码不一致"
                                    return@Button
                                }
                                if (newPassword.length < 6) {
                                    passwordError = "密码长度至少6位"
                                    return@Button
                                }
                                passwordError = null
                                isChangingPassword = true
                                scope.launch {
                                    try {
                                        val success = changePassword(newPassword)
                                        if (success) {
                                            Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show()
                                            oldPassword = ""
                                            newPassword = ""
                                            confirmPassword = ""
                                            passwordStrength = null
                                        } else {
                                            Toast.makeText(context, "密码修改失败", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "错误：${e.message}", Toast.LENGTH_LONG).show()
                                    } finally {
                                        isChangingPassword = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isChangingPassword && isLoggedIn && username != "admin"
                        ) {
                            if (isChangingPassword) {
                                MaterialExpressiveLoading(modifier = Modifier.size(24.dp))
                            } else {
                                Text("修改密码")
                            }
                        }
                        if (username == "admin") {
                            Text(
                                text = "超级管理员不能修改密码",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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
            } else if (errorMessage != null) {
                Text(
                    text = "加载失败，请稍后重试",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
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