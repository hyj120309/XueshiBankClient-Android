package app.xswallet.ui.pages.settings.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import app.xswallet.data.SecurePrefs
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    strings: AppStrings,
    isServerAvailable: Boolean,
    viewModel: AccountSecurityViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    val userInfo by viewModel.userInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isChangingPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordStrength by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoggedIn, username, token, isServerAvailable) {
        if (isLoggedIn && isServerAvailable) {
            viewModel.loadUserInfoWithDelay(username, token, baseUrl, 350)
        } else {
            viewModel.clear()
        }
    }

    suspend fun verifyOldPassword(oldPass: String): Boolean = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/login?usrname=${URLEncoder.encode(username, "UTF-8")}&passwd=${URLEncoder.encode(oldPass, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            responseCode == 200
        } catch (e: Exception) {
            false
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun changePassword(newPass: String): Result<Int> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/user/setpw?usrname=${URLEncoder.encode(username, "UTF-8")}&token=${URLEncoder.encode(token, "UTF-8")}&passwd=${URLEncoder.encode(newPass, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            // responseCode == 200
            if (responseCode == 200) {
                Result.success(responseCode)
            } else {
                Result.failure(Exception("HTTP_$responseCode"))
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
                    Icons.AutoMirrored.Filled.ArrowBack,
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
                            enabled = !isChangingPassword && isServerAvailable
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
                            enabled = !isChangingPassword && isServerAvailable,
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
                            enabled = !isChangingPassword && isServerAvailable,
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
                                        val oldValid = verifyOldPassword(oldPassword)
                                        if (!oldValid) {
                                            Toast.makeText(context, "原密码错误", Toast.LENGTH_SHORT).show()
                                            isChangingPassword = false
                                            return@launch
                                        }
                                        val result = changePassword(newPassword)
                                        if (result.isSuccess) {
                                            Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show()
                                            oldPassword = ""
                                            newPassword = ""
                                            confirmPassword = ""
                                            passwordStrength = null
                                        } else {
                                            val code = result.exceptionOrNull()?.message
                                            Toast.makeText(context, "密码修改失败: $code", Toast.LENGTH_SHORT).show()
                                            // Toast.makeText(context, "密码修改失败", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "错误：${e.message}", Toast.LENGTH_LONG).show()
                                    } finally {
                                        isChangingPassword = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isChangingPassword && isLoggedIn && username != "admin" && isServerAvailable
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
                        Icons.AutoMirrored.Filled.ExitToApp,
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
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = isServerAvailable
            ) {
                Text("去登录")
            }
        }
    }
}
