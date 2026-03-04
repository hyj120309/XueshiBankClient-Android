package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Composable
fun AddUserScreen(
    onBack: () -> Unit,
    token: String,
    strings: AppStrings,
    isServerAvailable: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    val permissionsList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionInput by remember { mutableStateOf("") }

    suspend fun addUser(): Boolean = withContext(Dispatchers.IO) {
        val permissionsNumbers = permissionsList.map { it.toInt() }
        val permissionsString = JSONArray(permissionsNumbers).toString()
        val urlString = "$baseUrl/api/user/add?" +
                "usrname=${URLEncoder.encode(username, "UTF-8")}&" +
                "passwd=${URLEncoder.encode(password, "UTF-8")}&" +
                "alias=${URLEncoder.encode(alias, "UTF-8")}&" +
                "permissions=${URLEncoder.encode(permissionsString, "UTF-8")}&" +
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
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
            enabled = !isLoading && isServerAvailable
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isServerAvailable
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("别名") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isServerAvailable
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showPermissionDialog = true },
                enabled = !isLoading && isServerAvailable,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加权限")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (permissionsList.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(permissionsList) { perm ->
                        AssistChip(
                            onClick = { },
                            label = { Text(perm) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { permissionsList.remove(perm) },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "删除", modifier = Modifier.size(14.dp))
                                }
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "点击加号添加权限组",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank() || alias.isBlank()) {
                    Toast.makeText(context, "请填写所有字段", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (permissionsList.isEmpty()) {
                    Toast.makeText(context, "请至少添加一个权限组", Toast.LENGTH_SHORT).show()
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
                            permissionsList.clear()
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
            enabled = !isLoading && isServerAvailable
        ) {
            if (isLoading) {
                MaterialExpressiveLoading(modifier = Modifier.size(24.dp))
            } else {
                Text("提交")
            }
        }
    }

    if (showPermissionDialog) {
        Dialog(onDismissRequest = { showPermissionDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "添加权限组",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = permissionInput,
                        onValueChange = { permissionInput = it.filter { c -> c.isDigit() } },
                        label = { Text("四位数字") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showPermissionDialog = false }) {
                            Text("取消")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (permissionInput.length == 4 && permissionInput.all { it.isDigit() }) {
                                    permissionsList.add(permissionInput)
                                    permissionInput = ""
                                    showPermissionDialog = false
                                } else {
                                    Toast.makeText(context, "请输入四位数字", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }
}