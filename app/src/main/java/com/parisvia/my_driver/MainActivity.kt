package com.parisvia.my_driver

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val securePreferences = SecurePreferences(this)

        if (securePreferences.isUserLoggedIn()) {
            // Kullanıcı giriş yapmış, doğrudan HomeActivity'ye yönlendir
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Kullanıcı giriş yapmamış, LoginActivity'ye yönlendir
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
