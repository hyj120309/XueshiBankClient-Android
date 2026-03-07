package app.xswallet.ui.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import app.xswallet.ui.AppStrings
import app.xswallet.ui.components.MaterialExpressiveLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

data class RankRecord(
    val rowid: Int,
    val studentId: Int,
    val changeAmount: Float,
    val changeReason: String,
    val admin: String,
    val timestamp: String
)

@Composable
fun RankRecordListScreen(
    onBack: () -> Unit,
    studentId: String,
    studentName: String,
    currentBalance: Int,
    strings: AppStrings,
    isServerAvailable: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var records by remember { mutableStateOf<List<RankRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun fetchRecords(studentId: String): List<RankRecord> = withContext(Dispatchers.IO) {
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
                val list = mutableListOf<RankRecord>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(RankRecord(
                        rowid = obj.getInt("rowid"),
                        studentId = obj.getInt("student_id"),
                        changeAmount = obj.getDouble("change_amount").toFloat(),
                        changeReason = obj.getString("change_reason"),
                        admin = obj.getString("admin"),
                        timestamp = obj.getString("local_time")
                    ))
                }
                list.sortedBy { it.timestamp }
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

    fun loadRecords() {
        if (!isServerAvailable) {
            errorMessage = "服务器不可用"
            isLoading = false
            return
        }
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                records = fetchRecords(studentId)
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(studentId) {
        delay(350)
        if (isServerAvailable) {
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
            }
            Text(
                text = "记录详情",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    text = "班级：${studentId.take(4)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "姓名：$studentName",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "学号：$studentId",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "金额：$currentBalance",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MaterialExpressiveLoading(modifier = Modifier.size(48.dp))
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("加载失败：$errorMessage", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (records.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无记录", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "资金变化趋势",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        RecordChart(
                            records = records.map { ChartRecord(it.changeAmount, it.timestamp) },
                            currentBalance = currentBalance,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
        }
    }
}