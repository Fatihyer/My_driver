// SecurePreferences.kt
package com.parisvia.my_driver

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserData(userId: String, userName: String, userEmail: String, authToken: String) {
        with(sharedPreferences.edit()) {
            putString("user_id", userId)
            putString("user_name", userName)
            putString("user_email", userEmail)
            putString("auth_token", authToken)
            apply()
        }
    }

    fun getUserId(): String? = sharedPreferences.getString("user_id", null)
    fun getUserName(): String? = sharedPreferences.getString("user_name", null)
    fun getUserEmail(): String? = sharedPreferences.getString("user_email", null)
    fun getAuthToken(): String? = sharedPreferences.getString("auth_token", null)

    fun clearUserData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getString("auth_token", null) != null
    }

}
