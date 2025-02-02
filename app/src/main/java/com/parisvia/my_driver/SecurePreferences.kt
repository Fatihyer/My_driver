// SecurePreferences.kt
package com.parisvia.my_driver

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecurePreferences(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "user_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserData(userId: String, userName: String, authToken: String) {
        with(sharedPreferences.edit()) {
            putString("user_id", userId)
            putString("user_name", userName)
            putString("auth_token", authToken)
            apply()
        }
    }

    fun getUserId(): String? = sharedPreferences.getString("user_id", null)
    fun getUserName(): String? = sharedPreferences.getString("user_name", null)
    fun getAuthToken(): String? = sharedPreferences.getString("auth_token", null)

    fun clearUserData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
