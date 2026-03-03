package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class Student(
    val name: String,
    val studentId: Int,
    val total: Int
)

@Composable
fun StudentListScreen(
    onBack: () -> Unit,
    username: String,
    token: String,
    strings: AppStrings,
    onStudentClick: (Int) -> Unit,
    isServerAvailable: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var isLoading by remember { mutableStateOf(true) }
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchUserPermissions(): String? = withContext(Dispatchers.IO) {
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
                        null
                    } else {
                        val json = JSONObject(response)
                        val permissionsField = json.get("permissions")
                        when (permissionsField) {
                            is JSONArray -> {
                                if (permissionsField.length() > 0) {
                                    permissionsField.getString(0)
                                } else {
                                    null
                                }
                            }
                            is String -> {
                                val trimmed = permissionsField.trim()
                                if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                                    trimmed.substring(1, trimmed.length - 1).trim().takeIf { it.isNotEmpty() }
                                } else {
                                    trimmed.takeIf { it.isNotEmpty() }
                                }
                            }
                            else -> null
                        }
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

    suspend fun fetchStudents(classId: String): List<Student> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/student/list?class=${URLEncoder.encode(classId, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            val inputStream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val response = inputStream.bufferedReader().use { it.readText() }
            if (responseCode == 200) {
                val jsonArray = JSONArray(response)
                val list = mutableListOf<Student>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(Student(
                        name = obj.getString("name"),
                        studentId = obj.getInt("student_id"),
                        total = obj.getInt("total")
                    ))
                }
                list
            } else {
                throw Exception("HTTP $responseCode: $response")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun deleteStudent(studentId: Int): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/student/del?" +
                "usrname=${URLEncoder.encode(username, "UTF-8")}&" +
                "stuid=${URLEncoder.encode(studentId.toString(), "UTF-8")}&" +
                "token=${URLEncoder.encode(token, "UTF-8")}"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            val response = connection.inputStream.bufferedReader().use { it.readText() }.trim()
            Pair(responseCode == 200, response)
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    fun loadData() {
        if (!isServerAvailable) {
            isLoading = false
            errorMessage = "服务器不可用"
            return
        }
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val classId = fetchUserPermissions()
                if (classId.isNullOrEmpty()) {
                    errorMessage = "当前用户没有权限组，无法加载学生"
                    students = emptyList()
                } else {
                    students = fetchStudents(classId)
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }
    var deleteTimer by remember { mutableStateOf(3) }

    LaunchedEffect(showDeleteDialog) {
        if (showDeleteDialog) {
            deleteTimer = 3
            while (deleteTimer > 0) {
                kotlinx.coroutines.delay(1000)
                deleteTimer--
            }
            showDeleteDialog = false
            studentToDelete = null
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
                text = "学生列表",
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
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("加载失败：$errorMessage", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { loadData() },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = isServerAvailable
            ) {
                Text("重试")
            }
        } else {
            if (students.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无学生", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onStudentClick(student.studentId) }
                                ) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "学号：${student.studentId}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "总金额：${student.total}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "查看记录 →",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            studentToDelete = student
                                            showDeleteDialog = true
                                        },
                                        enabled = isServerAvailable
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
        }
    }

    if (showDeleteDialog && studentToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                studentToDelete = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除学生 ${studentToDelete!!.name} 吗？\n此操作不可撤销。（${deleteTimer}秒后自动取消）") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                val (success, response) = deleteStudent(studentToDelete!!.studentId)
                                if (success) {
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                    loadData()
                                } else {
                                    Toast.makeText(context, "删除失败：$response", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "错误：${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                showDeleteDialog = false
                                studentToDelete = null
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
                        studentToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}