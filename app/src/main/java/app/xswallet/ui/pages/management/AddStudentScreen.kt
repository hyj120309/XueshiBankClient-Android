package app.xswallet.ui.pages.management

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Composable
fun AddStudentScreen(
    onBack: () -> Unit,
    username: String,
    token: String,
    strings: AppStrings,
    isServerAvailable: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val baseUrl = "https://bankapi.bcxs.qzz.io"

    var studentId by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    suspend fun addStudent(): Boolean = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/api/student/add?" +
                "usrname=${URLEncoder.encode(username, "UTF-8")}&" +
                "stuid=${URLEncoder.encode(studentId, "UTF-8")}&" +
                "stuname=${URLEncoder.encode(studentName, "UTF-8")}&" +
                "amount=${URLEncoder.encode(amount, "UTF-8")}&" +
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
                text = "添加学生",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("学号") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isServerAvailable,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = { Text("姓名") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isServerAvailable
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("初始金额") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isServerAvailable,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (studentId.isBlank() || studentName.isBlank() || amount.isBlank()) {
                    Toast.makeText(context, "请填写所有字段", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isLoading = true
                scope.launch {
                    try {
                        val success = addStudent()
                        if (success) {
                            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show()
                            studentId = ""
                            studentName = ""
                            amount = ""
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
}