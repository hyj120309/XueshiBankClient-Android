package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
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
    onStudentClick: (Int) -> Unit
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
                                val pattern = "\\d+".toRegex()
                                pattern.find(permissionsField)?.value
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

    fun loadData() {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val classId = fetchUserPermissions()
                if (classId.isNullOrBlank()) {
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
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                                .clickable { onStudentClick(student.studentId) }
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
                                Text(
                                    text = "查看记录 →",
                                    style = MaterialTheme.typography.bodySmall,
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