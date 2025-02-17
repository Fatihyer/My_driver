package com.parisvia.my_driver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d("APP_CRASH_TEST", "MainActivity başlatıldı")

            val securePreferences = SecurePreferences(applicationContext) // Uygulama bağlamı (Application Context)

            Log.d("APP_CRASH_TEST", "SecurePreferences başlatıldı")

            if (securePreferences.isUserLoggedIn()) {
                Log.d("APP_CRASH_TEST", "Kullanıcı giriş yapmış, HomeActivity'ye yönlendiriliyor")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("APP_CRASH_TEST", "Kullanıcı giriş yapmamış, LoginActivity'ye yönlendiriliyor")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            finish()

        } catch (e: Exception) {
            Log.e("APP_CRASH", "Hata oluştu: ${e.message}")
            Toast.makeText(this, "Uygulama açılırken hata oluştu!", Toast.LENGTH_LONG).show()
        }
    }
}
