package com.parisvia.my_driver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.parisvia.my_driver.util.SecurePreferences

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var securePreferences: SecurePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // SecurePreferences örneğini oluşturun
        securePreferences = SecurePreferences(this)

        // Kullanıcı adını alın
        val userName = securePreferences.getUserName()

        // Bileşenleri tanımlayın
        welcomeTextView = findViewById(R.id.welcomeTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Hoş geldin mesajını ayarlayın
        welcomeTextView.text = "Hoş geldin, $userName"

        // Çıkış Yap butonuna tıklama olayını ekleyin
        logoutButton.setOnClickListener {
            // Kullanıcı verilerini temizleyin
            securePreferences.clearUserData()

            // Giriş ekranına dönün
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
