package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Composable
fun AddUserScreen(
    onBack: () -> Unit,
    token: String,
    strings: AppStrings
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var permissions by remember { mutableStateOf("[]") }
    var isLoading by remember { mutableStateOf(false) }

    suspend fun addUser(): Boolean = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/user/add?" +
                "usrname=${URLEncoder.encode(username, "UTF-8")}&" +
                "passwd=${URLEncoder.encode(password, "UTF-8")}&" +
                "alias=${URLEncoder.encode(alias, "UTF-8")}&" +
                "permissions=${URLEncoder.encode(permissions, "UTF-8")}&" +
                "token=${URLEncoder.encode(token, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                true
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = strings.back)
            }
            Text(
                text = "添加用户",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("别名") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = permissions,
            onValueChange = { permissions = it },
            label = { Text("权限组 (JSON数组)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank() || alias.isBlank()) {
                    Toast.makeText(context, "请填写所有字段", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isLoading = true
                scope.launch {
                    try {
                        val success = addUser()
                        if (success) {
                            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show()
                            username = ""
                            password = ""
                            alias = ""
                            permissions = "[]"
                        } else {
                            Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "错误：${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                MaterialExpressiveLoading(modifier = Modifier.size(24.dp))
            } else {
                Text("提交")
            }
        }
    }
}