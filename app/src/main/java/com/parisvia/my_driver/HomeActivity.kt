package com.parisvia.my_driver

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parisvia.my_driver.adapters.TransferAdapter
import com.parisvia.my_driver.model.TransferResponse
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etDateRange: EditText
    private lateinit var btnFilter: Button
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.rvTransfers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        etDateRange = findViewById(R.id.etDateRange)
        btnFilter = findViewById(R.id.btnFilter)

        // Tarih seçme işlemi
        etDateRange.setOnClickListener {
            showDatePickerDialog()
        }

        // Filtreleme butonu
        btnFilter.setOnClickListener {
            if (selectedDate.isNotEmpty()) {
                fetchTransfers(selectedDate)
            } else {
                Toast.makeText(this, "Lütfen tarih seçin!", Toast.LENGTH_SHORT).show()
            }
        }

        // Varsayılan olarak bugünün tarihini getir
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        fetchTransfers(today)
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
                selectedDate = formattedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun fetchTransfers(date: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.getTransfers("Bearer " + SecurePreferences(this@HomeActivity).getAuthToken(), date)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null) {
                    recyclerView.adapter = TransferAdapter(response.body()!!.transfers)
                } else {
                    Toast.makeText(this@HomeActivity, "Veri çekilemedi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
