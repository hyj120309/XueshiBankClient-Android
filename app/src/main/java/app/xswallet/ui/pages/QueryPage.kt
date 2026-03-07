package app.xswallet.ui.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import java.util.*

data class StudentInfo(
    val name: String,
    val studentId: Int,
    val total: Int
)

data class Record(
    val rowid: Int,
    val studentId: Int,
    val changeAmount: Float,
    val changeReason: String,
    val admin: String,
    val timestamp: String
)

@Composable
fun QueryPage(
    isLoggedIn: Boolean,
    username: String,
    token: String,
    strings: AppStrings,
    isServerAvailable: Boolean,
    initialStudentId: String = "",
    onSearchPerformed: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var searchText by remember { mutableStateOf(initialStudentId) }
    var isLoading by remember { mutableStateOf(false) }
    var studentInfo by remember { mutableStateOf<StudentInfo?>(null) }
    var records by remember { mutableStateOf<List<Record>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun searchStudent(studentIdInput: String) {
        if (studentIdInput.length < 4) {
            errorMessage = "学号至少4位"
            return
        }
        val classId = studentIdInput.substring(0, 4)
        val fullId = studentIdInput.toIntOrNull()
        if (fullId == null) {
            errorMessage = "学号必须是数字"
            return
        }

        isLoading = true
        errorMessage = null
        studentInfo = null
        records = emptyList()

        try {
            val studentList = withContext(Dispatchers.IO) {
                val urlString = "$baseUrl/api/student/list?class=${URLEncoder.encode(classId, "UTF-8")}"
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
                        val list = mutableListOf<StudentInfo>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            list.add(StudentInfo(
                                name = obj.getString("name"),
                                studentId = obj.getInt("student_id"),
                                total = obj.getInt("total")
                            ))
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

            val student = studentList.find { it.studentId == fullId }
            if (student == null) {
                errorMessage = "未找到该学生"
                isLoading = false
                return
            }
            studentInfo = student

            val recordList = withContext(Dispatchers.IO) {
                val urlString = "$baseUrl/api/record/list?stuid=${URLEncoder.encode(studentIdInput, "UTF-8")}"
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
                        val list = mutableListOf<Record>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            list.add(Record(
                                rowid = obj.getInt("rowid"),
                                studentId = obj.getInt("student_id"),
                                changeAmount = obj.getDouble("change_amount").toFloat(),
                                changeReason = obj.getString("change_reason"),
                                admin = obj.getString("admin"),
                                timestamp = obj.getString("local_time")
                            ))
                        }
                        list
                    } else if (responseCode == 404) {
                        emptyList()
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
            records = recordList.sortedBy { it.timestamp }
        } catch (e: Exception) {
            errorMessage = "查询失败：${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(initialStudentId) {
        if (initialStudentId.isNotEmpty() && studentInfo == null && !isLoading && isServerAvailable) {
            searchText = initialStudentId
            searchStudent(initialStudentId)
            onSearchPerformed()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                text = "查询",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("输入学号") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    IconButton(
                        onClick = { scope.launch { searchStudent(searchText) } },
                        enabled = isServerAvailable && !isLoading
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                },
                isError = errorMessage != null,
                supportingText = errorMessage?.let { { Text(it) } },
                enabled = isServerAvailable
            )
        }

        item {
            Text(
                text = "格式：班级+学号\n例子：2401班1号，填写240101",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
                }
            }
        }

        studentInfo?.let { student ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "班级：${student.studentId.toString().substring(0, 4)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "姓名：${student.name}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "学号：${student.studentId}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "金额：${student.total}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (records.isNotEmpty()) {
                item {
                    Text(
                        text = "资金变化趋势",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    RecordChart(
                        records = records.map { ChartRecord(it.changeAmount, it.timestamp) },
                        currentBalance = student.total,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Text(
                    text = "记录明细",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (records.isEmpty()) {
                item {
                    Text(
                        text = "无",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(records) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (record.changeAmount > 0) "收入" else "支出",
                                    fontWeight = FontWeight.Medium,
                                    color = if (record.changeAmount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = String.format(Locale.US, "%+.2f", record.changeAmount),
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.changeAmount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = "原因：${record.changeReason}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "操作人：${record.admin}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "时间：${record.timestamp}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}