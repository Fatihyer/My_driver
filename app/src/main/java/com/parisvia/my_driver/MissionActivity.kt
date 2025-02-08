package com.parisvia.my_driver

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MissionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_detail)

        // **Geri Butonu (Return Back)**
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val transferId = intent.getIntExtra("transfer_id", -1)
        val textViewTransferDetail = findViewById<TextView>(R.id.textViewTransferDetail)

        if (transferId != -1) {
            fetchTransferDetail(transferId)
            textViewTransferDetail.visibility = View.GONE
        } else {
            textViewTransferDetail.visibility = View.VISIBLE
            textViewTransferDetail.text = "Veri bulunamadı!"
        }

    }
