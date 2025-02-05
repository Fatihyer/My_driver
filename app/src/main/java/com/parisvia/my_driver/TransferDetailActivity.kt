package com.parisvia.my_driver

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.parisvia.my_driver.model.TransferDetailResponse
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

        val transferId = intent.getIntExtra("transfer_id", -1)

        if (transferId != -1) {
            fetchTransferDetail(transferId)
        } else {
            findViewById<TextView>(R.id.textViewTransferDetail).text = "Veri bulunamadı!"
        }
    }

    private fun fetchTransferDetail(transferId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@TransferDetailActivity).getAuthToken()
                Log.d("DEBUG", "API çağrısı yapılıyor. Token: $token, ID: $transferId")

                val response = ApiClient.apiService.getTransferDetail(token, transferId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val transferDetail = response.body()

                        transferDetail?.let {
                            Log.d("TRANSFER_DETAIL", "Başlangıç Tarihi: ${it.start_date}, Durum: ${it.status}")

                            // UI'yi güncelle
                            findViewById<TextView>(R.id.textViewTransferDetail).text =
                                "Başlangıç Tarihi: ${it.start_date}"
                            findViewById<TextView>(R.id.textViewFrom).text = "Nereden: ${it.from}"
                            findViewById<TextView>(R.id.textViewTo).text = "Nereye: ${it.to}"
                            findViewById<TextView>(R.id.textViewStatus).text = "Durum: ${it.status}"
                        }
                    } else {
                        Log.e("API_ERROR", "Veri çekilemedi! HTTP Code: ${response.code()}")
                        findViewById<TextView>(R.id.textViewTransferDetail).text = "API Hatası: ${response.code()}"
                    }
                }
            } catch (e: HttpException) {
                Log.e("API_FAILURE", "HTTP Hatası: ${e.message}")
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textViewTransferDetail).text = "HTTP Hatası: ${e.message}"
                }
            } catch (e: IOException) {
                Log.e("API_FAILURE", "Bağlantı hatası! İnternet bağlantınızı kontrol edin.")
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textViewTransferDetail).text = "Bağlantı hatası! İnternet bağlantınızı kontrol edin."
                }
            } catch (e: Exception) {
                Log.e("API_FAILURE", "Bilinmeyen hata: ${e.message}")
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textViewTransferDetail).text = "Bilinmeyen hata: ${e.message}"
                }
            }
        }
    }
}
