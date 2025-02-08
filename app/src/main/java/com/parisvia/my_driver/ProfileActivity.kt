package com.parisvia.my_driver

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        try {
            // Kullanıcı bilgilerini almak için SecurePreferences kullan
            val securePreferences = SecurePreferences(this)
            val userId = securePreferences.getUserId() ?: "Bilinmiyor"
            val userName = securePreferences.getUserName() ?: "Bilinmiyor"
            val userEmail = securePreferences.getUserEmail() ?: "Bilinmiyor"
            val userToken = securePreferences.getAuthToken() ?: "Bilinmiyor"
            // UI bileşenlerini bul ve bilgileri ekrana yazdır
            findViewById<TextView>(R.id.tvUserId)?.text = "Kullanıcı ID: $userId"
            findViewById<TextView>(R.id.tvUserName)?.text = "Ad: $userName"
            findViewById<TextView>(R.id.tvUserEmail)?.text = "E-posta: $userEmail"
            findViewById<TextView>(R.id.tvUserToken)?.text = "E-posta: $userToken"
            Log.d("LOGIN_TOKEN", "Giriş Tokenı: $userToken")
        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
