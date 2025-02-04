package com.parisvia.my_driver

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parisvia.my_driver.adapters.TransferAdapter
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etDateRange: EditText
    private lateinit var btnYesterday: Button
    private lateinit var btnToday: Button
    private lateinit var btnTomorrow: Button
    private var selectedDateOption: String = "today" // Varsayılan olarak "Today"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.rvTransfers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        etDateRange = findViewById(R.id.etDateRange)
        btnYesterday = findViewById(R.id.btnYesterday)
        btnToday = findViewById(R.id.btnToday)
        btnTomorrow = findViewById(R.id.btnTomorrow)

        // Tarih seçme işlemi (manuel giriş)
        etDateRange.setOnClickListener {
            showDatePickerDialog()
            resetButtonColors()
        }

        // Yesterday Butonu
        btnYesterday.setOnClickListener {
            selectedDateOption = "yesterday"
            val yesterday = getDate(-1)
            etDateRange.setText(yesterday)
            fetchTransfers(selectedDateOption)
            updateButtonColors(btnYesterday)
        }

        // Today Butonu
        btnToday.setOnClickListener {
            selectedDateOption = "today"
            val today = getDate(0)
            etDateRange.setText(today)
            fetchTransfers(selectedDateOption)
            updateButtonColors(btnToday)
        }

        // Tomorrow Butonu
        btnTomorrow.setOnClickListener {
            selectedDateOption = "tomorrow"
            val tomorrow = getDate(1)
            etDateRange.setText(tomorrow)
            fetchTransfers(selectedDateOption)
            updateButtonColors(btnTomorrow)
        }

        // Varsayılan olarak bugünün tarihini EditText'e yaz ve verileri getir
        val today = getDate(0)
        etDateRange.setText(today)
        fetchTransfers("today")
        updateButtonColors(btnToday) // İlk başta today seçili
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etDateRange.setText(formattedDate)
                fetchTransfers(formattedDate) // Tarih seçildiğinde API'yi çağır
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun fetchTransfers(dateOption: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.getTransfers(
                "Bearer " + SecurePreferences(this@HomeActivity).getAuthToken(),
                dateOption // API'ye gönderilecek tarih verisi
            )

            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    recyclerView.adapter = TransferAdapter(response.body()!!.transfers)
                } else {
                    Toast.makeText(this@HomeActivity, "Veri çekilemedi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getDate(daysOffset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, daysOffset)
        return dateFormat.format(calendar.time)
    }

    // Seçili butonu vurgulamak için renk değiştir
    private fun updateButtonColors(selectedButton: Button) {
        resetButtonColors()
        selectedButton.setBackgroundColor(Color.parseColor("#FF9800")) // Turuncu renk (seçili buton)
        selectedButton.setTextColor(Color.WHITE)
    }

    // Bütün butonları varsayılan renge döndür
    private fun resetButtonColors() {
        val defaultColor = Color.parseColor("#DDDDDD") // Gri ton
        val textColor = Color.BLACK

        btnYesterday.setBackgroundColor(defaultColor)
        btnYesterday.setTextColor(textColor)

        btnToday.setBackgroundColor(defaultColor)
        btnToday.setTextColor(textColor)

        btnTomorrow.setBackgroundColor(defaultColor)
        btnTomorrow.setTextColor(textColor)
    }
}
