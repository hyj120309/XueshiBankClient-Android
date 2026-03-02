package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class User(
    val username: String,
    val alias: String,
    val permissions: List<String>
)

@Composable
fun ManageUserScreen(
    onBack: () -> Unit,
    token: String,
    strings: AppStrings
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun fetchUsers(): List<User> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/user/list?token=${URLEncoder.encode(token, "UTF-8")}"
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
                val list = mutableListOf<User>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val username = obj.getString("username")
                    val alias = obj.getString("alias")
                    val permissionsStr = obj.getString("permissions")
                    val permissions = JSONArray(permissionsStr).let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    }
                    list.add(User(username, alias, permissions))
                }
                // 按权限组大小排序（从小到大）
                list.sortedBy { it.permissions.size }
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

    suspend fun deleteUser(username: String): Boolean = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/user/del?usrname=${URLEncoder.encode(username, "UTF-8")}&token=${URLEncoder.encode(token, "UTF-8")}"
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
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    fun loadUsers() {
        isLoading = true
        error = null
        scope.launch {
            try {
                users = fetchUsers()
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadUsers()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }
    var deleteTimer by remember { mutableStateOf(3) }

    LaunchedEffect(showDeleteDialog) {
        if (showDeleteDialog) {
            deleteTimer = 3
            while (deleteTimer > 0) {
                kotlinx.coroutines.delay(1000)
                deleteTimer--
            }
            showDeleteDialog = false
            userToDelete = null
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
                text = "管理用户",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("加载失败：$error", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = user.username,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "别名：${user.alias}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "权限组：${user.permissions.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            IconButton(
                                onClick = {
                                    userToDelete = user
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "删除",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                userToDelete = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除用户 ${userToDelete!!.username} 吗？\n此操作不可撤销。（${deleteTimer}秒后自动取消）") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                val success = deleteUser(userToDelete!!.username)
                                if (success) {
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                    loadUsers() // 刷新列表
                                } else {
                                    Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "错误：${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                showDeleteDialog = false
                                userToDelete = null
                            }
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        userToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}