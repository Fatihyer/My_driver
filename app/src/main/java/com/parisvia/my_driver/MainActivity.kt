// MainActivity.kt
package com.parisvia.my_driver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.parisvia.my_driver.model.LoginResponse
import com.parisvia.my_driver.viewmodel.MainViewModel
import com.parisvia.my_driver.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        // Observe LiveData from ViewModel
        viewModel.loginResponse.observe(this, Observer { response ->
            handleLoginResponse(response)
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            showToast(message)
        })

        viewModel.networkError.observe(this, Observer { error ->
            showToast(error)
        })

        // Set click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                showToast("Lütfen tüm alanları doldurun")
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Çıkış işlemlerini burada gerçekleştirin
                val securePreferences = SecurePreferences(this)
                securePreferences.clearUserData()

                // Giriş ekranına dönün
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun handleLoginResponse(response: LoginResponse?) {
        response?.let {
            val user = it.user
            val token = it.token
            val securePreferences = SecurePreferences(this)
            securePreferences.saveUserData(user.id.toString(), user.name, token)
            showToast("Giriş başarılı! Hoş geldin, ${user.name}")
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } ?: showToast("Giriş yanıtı boş")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
