package com.parisvia.my_driver

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.parisvia.my_driver.model.MissionResponse
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MissionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val transferId = intent.getStringExtra("transfer_id")?.toIntOrNull() ?: -1

        val textViewMissionDetail = findViewById<TextView>(R.id.textViewMissionDetail)

        if (transferId != -1) {
            fetchMissionDetails(transferId)
            textViewMissionDetail.visibility = View.GONE
        } else {
            textViewMissionDetail.visibility = View.VISIBLE
            textViewMissionDetail.text = "Görev bulunamadı!"
        }
    }

    private fun fetchMissionDetails(transferId: Int,) {
        lifecycleScope.launch(Dispatchers.IO) {

    }}
}
