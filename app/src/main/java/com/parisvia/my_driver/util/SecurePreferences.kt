package com.parisvia.my_driver.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePreferences(context: Context) {

    // Create a MasterKey for encryption/decryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Initialize EncryptedSharedPreferences
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context, // context
        "user_prefs", // fileName
        masterKey, // masterKey
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // keyEncryptionScheme
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // valueEncryptionScheme
    )

    fun saveUserData(userId: String, userName: String, authToken: String) {
        with(sharedPreferences.edit()) {
            putString("user_id", userId)
            putString("user_name", userName)
            putString("auth_token", authToken)
            apply()
        }
    }
    fun clearUserData() {
        with(sharedPreferences.edit()) {
            remove("user_id")
            remove("user_name")
            remove("auth_token")
            apply()
        }
    }
    fun getUserId(): String? = sharedPreferences.getString("user_id", null)
    fun getUserName(): String? = sharedPreferences.getString("user_name", null)
    fun getAuthToken(): String? = sharedPreferences.getString("auth_token", null)


}
