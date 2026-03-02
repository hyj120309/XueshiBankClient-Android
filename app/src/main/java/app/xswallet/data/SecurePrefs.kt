package app.xswallet.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecurePrefs {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"

    private fun getEncryptedPrefs(context: Context) =
        EncryptedSharedPreferences.create(
            PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveUser(context: Context, username: String, password: String) {
        getEncryptedPrefs(context).edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getUser(context: Context): Pair<String?, String?> {
        val prefs = getEncryptedPrefs(context)
        return Pair(prefs.getString(KEY_USERNAME, null), prefs.getString(KEY_PASSWORD, null))
    }

    fun clearUser(context: Context) {
        getEncryptedPrefs(context).edit().clear().apply()
    }

    fun hasUser(context: Context): Boolean {
        val (username, password) = getUser(context)
        return !username.isNullOrEmpty() && !password.isNullOrEmpty()
    }
}