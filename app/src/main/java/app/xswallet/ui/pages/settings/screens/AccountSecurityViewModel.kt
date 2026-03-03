package app.xswallet.ui.pages.settings.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AccountSecurityViewModel : ViewModel() {
    private val _userInfo = MutableStateFlow<Map<String, String>?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private var loadedUsername = ""

    fun loadUserInfoWithDelay(username: String, token: String, baseUrl: String, delayMs: Long) {
        viewModelScope.launch {
            delay(delayMs)
            if (loadedUsername != username || _userInfo.value == null) {
                loadUserInfo(username, token, baseUrl)
            }
        }
    }

    private suspend fun loadUserInfo(username: String, token: String, baseUrl: String) {
        _isLoading.value = true
        _errorMessage.value = null
        try {
            val info = fetchUserInfo(username, token, baseUrl)
            _userInfo.value = info
            loadedUsername = username
        } catch (e: Exception) {
            _errorMessage.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchUserInfo(username: String, token: String, baseUrl: String): Map<String, String>? = withContext(Dispatchers.IO) {
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
                        mapOf("username" to "admin", "alias" to "超级管理员", "permissions" to "")
                    } else {
                        val json = JSONObject(response)
                        val username = json.getString("username")
                        val alias = json.getString("alias")
                        val permissionsStr = try {
                            val permissionsArray = json.getJSONArray("permissions")
                            (0 until permissionsArray.length()).joinToString(", ") { permissionsArray.getString(it) }
                        } catch (e: Exception) {
                            val raw = json.optString("permissions", "")
                            if (raw.startsWith("[") && raw.endsWith("]")) {
                                val inner = raw.substring(1, raw.length - 1)
                                if (inner.isBlank()) "" else inner
                            } else {
                                raw
                            }
                        }
                        mapOf("username" to username, "alias" to alias, "permissions" to permissionsStr)
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

    fun clear() {
        _userInfo.value = null
        loadedUsername = ""
    }
}