package com.parisvia.my_driver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.parisvia.my_driver.model.LoginResponse
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.login(email, password)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse: LoginResponse = response.body()!!
                        val user = loginResponse.user
                        val token = loginResponse.token

                        // Kullanıcı bilgilerini kaydet
                        SecurePreferences(this@LoginActivity).saveUserData(user.id.toString(), user.name, user.email, token)



                        Toast.makeText(this@LoginActivity, "Giriş başarılı! Hoşgeldin, ${user.name}", Toast.LENGTH_SHORT).show()

                        // Ana ekrana yönlendir
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Giriş başarısız! Hatalı email veya şifre.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Bağlantı hatası! ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
