package com.parisvia.my_driver

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.parisvia.my_driver.model.TransferDetailResponse
import com.parisvia.my_driver.model.StatusUpdateRequest
import android.graphics.Typeface
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TransferDetailActivity : BaseActivity() {
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

    private fun fetchTransferDetail(transferId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@TransferDetailActivity).getAuthToken()
                val response = ApiClient.apiService.getTransferDetail(token, transferId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val transferDetail = response.body()

                        transferDetail?.let {
                            val transfer = it.transfer
                            val timetable = it.transfer.timetable

                            val tableTimetable = findViewById<TableLayout>(R.id.tableTimetable)
                            val tableTrajets = findViewById<TableLayout>(R.id.tableTrajets)
                            val confirmButton = findViewById<Button>(R.id.buttonConfirm)
                            val textViewStatus = findViewById<TextView>(R.id.textViewStatus)
                            val misafirTextView = findViewById<TextView>(R.id.textViewMisafir)
                            val MissionBtn: Button =findViewById(R.id.btnMission)
                            val buttonOpenMaps = findViewById<Button>(R.id.buttonOpenMaps)



                            // ✅ **Mevcut tabloları temizle**
                            if (tableTimetable.childCount > 1) {
                                tableTimetable.removeViews(1, tableTimetable.childCount - 1)
                            }
                            if (tableTrajets.childCount > 1) {
                                tableTrajets.removeViews(1, tableTrajets.childCount - 1)
                            }
                            MissionBtn.setOnClickListener {

                                val intent = Intent(this@TransferDetailActivity,MissionActivity::class.java)
                                intent.putExtra("transfer_id", transfer.id) // Transfer ID'yi yeni aktiviteye gönder
                                startActivity(intent)
                            }

                            ////harita
                            Log.d("MAPS_DATA", "Maps Origin: ${transfer.origin}")
                            Log.d("MAPS_DATA", "Maps Destination: ${transfer.destination}")
                            Log.d("MAPS_DATA", "Maps Waypoints (String): ${transfer.waypoints}")


                                buttonOpenMaps.visibility = View.VISIBLE
                                buttonOpenMaps.setOnClickListener {
                                    openGoogleMaps(transfer.origin, transfer.destination, transfer.waypoints)
                                }
                            


                            // ✅ **Zaman Çizelgesi Tablosuna verileri ekleyelim**
                            val newRow = TableRow(this@TransferDetailActivity)
                            listOf(
                                timetable.ofisStart,
                                timetable.sur_place,
                                timetable.startDate,
                                timetable.endDate
                            ).forEach { value ->
                                val textView = TextView(this@TransferDetailActivity).apply {
                                    text = value ?: "N/A"
                                    gravity = Gravity.CENTER
                                    setPadding(8, 8, 8, 8)
                                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                    setBackgroundColor(Color.WHITE)
                                    setTextColor(Color.BLACK)
                                    setBackgroundResource(R.drawable.table_border)
                                }
                                newRow.addView(textView)
                            }
                            tableTimetable.addView(newRow)

                            // ✅ **Trajets bilgilerini tabloya ekleyelim**
                            if (!it.trajets.isNullOrEmpty()) {
                                for (trajet in it.trajets) {
                                    val trajetRow = TableRow(this@TransferDetailActivity)

                                    val textTrajetDetails = TextView(this@TransferDetailActivity).apply {
                                        text = trajet.details ?: "Bilinmiyor"
                                      //  gravity = Gravity.CENTER
                                        setPadding(8, 8, 8, 8)
                                        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                        setBackgroundColor(Color.WHITE)
                                        setTextColor(Color.BLACK)
                                        setBackgroundResource(R.drawable.table_border)
                                    }

                                    trajetRow.addView(textTrajetDetails)
                                    tableTrajets.addView(trajetRow)
                                }
                            } else {
                                val emptyRow = TableRow(this@TransferDetailActivity)
                                val emptyText = TextView(this@TransferDetailActivity).apply {
                                    text = "Trajet bilgisi bulunamadı!"
                                    gravity = Gravity.CENTER
                                    setPadding(8, 8, 8, 8)
                                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                    setBackgroundColor(Color.WHITE)
                                    setTextColor(Color.RED)
                                    setBackgroundResource(R.drawable.table_border)
                                }
                                emptyRow.addView(emptyText)
                                tableTrajets.addView(emptyRow)
                            }

                            // ✅ **Misafir (Müşteri) bilgilerini güncelle**
                            val misafirList = it.misafirler.joinToString("\n") { misafir ->
                                "${misafir.name} ${misafir.surname} - ${misafir.phone}"
                            }
                            misafirTextView.text = misafirList




                            // ✅ **Transfer Detaylarını Güncelle**
                            findViewById<TextView>(R.id.textViewServiceType).text = "Service: ${transfer.servicetype.name ?: "Not Set"}"
                            findViewById<TextView>(R.id.textViewVehicule).text = "Vehicule: ${transfer.vehicule.name ?: "Not Set"}"

                            val textViewFrom = findViewById<TextView>(R.id.textViewFrom)
                            val targetText = transfer.from
                            val fullText = "Lieu de Prise en Charge: $targetText"
                            val spannable = SpannableStringBuilder(fullText)
                            val start = fullText.indexOf(targetText)
                            val end = start + targetText.length

                            spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            textViewFrom.text = spannable

                            findViewById<TextView>(R.id.textViewTo).text = "Lieux de depose: ${transfer.target}"
                            textViewStatus.text = transfer.status.name
                            findViewById<TextView>(R.id.textViewPax).text = "Passager(s): ${transfer.pax}"
                            findViewById<TextView>(R.id.textViewComments).text="Comments: ${transfer.comments ?: "No comments"}"

                            // ✅ **Status ID'ye göre renk değişimi ve buton görünürlüğü**
                            if (transfer.status_id==3) {

                                    textViewStatus.setTextColor(Color.BLUE) // Mavi
                                    confirmButton.visibility = View.GONE
                                    MissionBtn.visibility = View.VISIBLE
                                }
                                else  {
                                    textViewStatus.setTextColor(Color.BLACK) // Siyah
                                    confirmButton.visibility = View.VISIBLE
                                }

                            confirmButton.setOnClickListener {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val updateRequest = StatusUpdateRequest(status_id = 3)
                                        val updateResponse = ApiClient.apiService.confirmTransfer(token, transfer.id, updateRequest)

                                        withContext(Dispatchers.Main) {
                                            if (updateResponse.isSuccessful) {
                                                confirmButton.visibility = View.GONE
                                                recreate() // Sayfayı yenile
                                            } else {
                                                Log.e("UPDATE_ERROR", "Hata: ${updateResponse.code()}")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Log.e("UPDATE_EXCEPTION", "Güncelleme hatası: ${e.message}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textViewTransferDetail).text = "Bağlantı hatası!"
                }
            }
        }
    }
    private fun openGoogleMaps(origin: String, destination: String, waypoints: String?) {
        val uriString = buildGoogleMapsUri(origin, destination, waypoints)
        Log.d("MAPS_INTENT", "Google Maps URI: $uriString") // ✅ URI'yi Logcat'te kontrol etmek için

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
        intent.setPackage("com.google.android.apps.maps") // 📌 Google Haritalar uygulamasıyla açmayı dene

        val resolveInfo = intent.resolveActivity(packageManager)

        if (resolveInfo != null) {
            Log.d("MAPS_INTENT", "Google Haritalar uygulaması bulundu: $resolveInfo")
            startActivity(intent)
        } else {
            Log.e("MAPS_INTENT", "Google Haritalar YÜKLÜ DEĞİL! Varsayılan tarayıcıyı açıyorum.")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
            startActivity(browserIntent)
        }
    }




    private fun buildGoogleMapsUri(origin: String, destination: String, waypoints: String?): String {
        val baseUri = "https://www.google.com/maps/dir/?api=1"

        val encodedOrigin = Uri.encode(origin)
        val encodedDestination = Uri.encode(destination)
        val encodedWaypoints = waypoints?.let { Uri.encode(it) } ?: ""

        return if (encodedWaypoints.isNotEmpty()) {
            "$baseUri&origin=$encodedOrigin&destination=$encodedDestination&waypoints=$encodedWaypoints"
        } else {
            "$baseUri&origin=$encodedOrigin&destination=$encodedDestination"
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
