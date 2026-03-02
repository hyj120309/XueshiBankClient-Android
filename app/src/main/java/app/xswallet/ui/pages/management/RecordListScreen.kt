package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class Record(
    val rowid: Int,
    val studentId: Int,
    val changeAmount: Float,
    val changeReason: String,
    val admin: String
)

@Composable
fun RecordListScreen(
    onBack: () -> Unit,
    username: String,
    token: String,
    strings: AppStrings,
    initialStudentId: String = ""
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var studentIdInput by remember { mutableStateOf(initialStudentId) }
    var records by remember { mutableStateOf<List<Record>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchRecords(studentId: String): List<Record> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/record/list?stuid=${URLEncoder.encode(studentId, "UTF-8")}"
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
                val list = mutableListOf<Record>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(Record(
                        rowid = obj.getInt("rowid"),
                        studentId = obj.getInt("student_id"),
                        changeAmount = obj.getDouble("change_amount").toFloat(),
                        changeReason = obj.getString("change_reason"),
                        admin = obj.getString("admin")
                    ))
                }
                list
            } else if (responseCode == 404) {
                emptyList()
            } else {
                throw Exception("HTTP $responseCode: $response")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun deleteRecord(recordId: Int, studentId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/record/del?" +
                "usrname=${URLEncoder.encode(username, "UTF-8")}&" + // 修正为 usrname
                "stuid=${URLEncoder.encode(studentId, "UTF-8")}&" +
                "recordid=${URLEncoder.encode(recordId.toString(), "UTF-8")}&" +
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

    fun loadRecords() {
        if (studentIdInput.isBlank()) {
            Toast.makeText(context, "请输入学号", Toast.LENGTH_SHORT).show()
            return
        }
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                records = fetchRecords(studentIdInput)
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<Record?>(null) }
    var deleteTimer by remember { mutableStateOf(3) }

    LaunchedEffect(showDeleteDialog) {
        if (showDeleteDialog) {
            deleteTimer = 3
            while (deleteTimer > 0) {
                kotlinx.coroutines.delay(1000)
                deleteTimer--
            }
            showDeleteDialog = false
            recordToDelete = null
        }
    }

    LaunchedEffect(initialStudentId) {
        if (initialStudentId.isNotEmpty()) {
            studentIdInput = initialStudentId
            loadRecords()
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
                text = "查询记录",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = studentIdInput,
                onValueChange = { studentIdInput = it },
                label = { Text("学号") },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { loadRecords() },
                enabled = !isLoading
            ) {
                Text("查询")
            }
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
        } else {
            if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无记录", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(records) { record ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "学号：${record.studentId}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "金额：${record.changeAmount}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (record.changeAmount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "原因：${record.changeReason}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "操作人：${record.admin}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            recordToDelete = record
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
        }
    }

    if (showDeleteDialog && recordToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                recordToDelete = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条记录吗？\n此操作不可撤销。（${deleteTimer}秒后自动取消）") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                val (success, response) = deleteRecord(recordToDelete!!.rowid, recordToDelete!!.studentId.toString())
                                if (success) {
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                    loadRecords()
                                } else {
                                    Toast.makeText(context, "删除失败：$response", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "错误：${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                showDeleteDialog = false
                                recordToDelete = null
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
                        recordToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}