package app.xswallet.ui.pages

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OverviewViewModel : ViewModel() {
    var htmlContent = mutableStateOf<String?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    private var loaded = false

    fun loadAnnouncement(baseUrl: String) {
        if (loaded) return
        loaded = true
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = fetchAnnouncement(baseUrl)
                htmlContent.value = result
            } catch (e: Exception) {
                errorMessage.value = "获取公告失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun refresh(baseUrl: String) {
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = fetchAnnouncement(baseUrl)
                htmlContent.value = result
            } catch (e: Exception) {
                errorMessage.value = "获取公告失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun fetchAnnouncement(baseUrl: String): String = withContext(Dispatchers.IO) {
        val urlString = "$baseUrl/pub"
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
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
}