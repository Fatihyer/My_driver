package com.parisvia.my_driver

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.parisvia.my_driver.model.MissionResponse
import com.parisvia.my_driver.model.Mission

import com.parisvia.my_driver.model.StartMissionRequest
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.Manifest

class MissionActivity : BaseActivity() {

    private lateinit var locationManager: LocationManager
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val btnBack = findViewById<Button>(R.id.btnBack)
        val textViewMissionDetail = findViewById<TextView>(R.id.textViewMissionDetail)
        val editTextKm = findViewById<EditText>(R.id.editTextKm)
        val buttonStartMission = findViewById<Button>(R.id.buttonStartMission)
        val buttonOnBoard = findViewById<Button>(R.id.buttonOnBoard)
        val buttonFinishMission = findViewById<Button>(R.id.buttonFinishMission)
        val btnSurplace = findViewById<Button>(R.id.btnSurplace)


        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val transferId = intent.getIntExtra("transfer_id", -1)
        Log.d("DEBUG_TRANSFER", "Gönderilen Transfer ID: $transferId")
        if (transferId != -1) {
            fetchMissionDetails(transferId, textViewMissionDetail, editTextKm, buttonStartMission, btnSurplace, buttonOnBoard, buttonFinishMission,)
        } else {
            textViewMissionDetail.text = "Görev bulunamadı!"
            buttonStartMission.visibility = View.GONE
            buttonOnBoard.visibility = View.GONE
            buttonFinishMission.visibility = View.GONE

        }

        buttonStartMission.setOnClickListener {
            val departKm = editTextKm.text.toString().toIntOrNull()
            if (departKm != null) {
                getLastKnownLocation { lat, long ->
                    if (lat != null && long != null) {
                        startMission(transferId, departKm, lat, long)
                    } else {
                        Toast.makeText(this, "Konum alınamadı!", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Lütfen kilometreyi girin!", Toast.LENGTH_LONG).show()
            }
        }
        btnSurplace.setOnClickListener {
            getLastKnownLocation { lat, long ->
                if (lat != null && long != null) {
                    Log.d("SURPLACE_CLICK", "Son Konum: Lat: $lat, Long: $long")
                    startSurplaceMission(transferId, lat, long)
                } else {
                    Log.e("SURPLACE_CLICK", "Konum alınamadı! Varsayılan değerler kullanılacak.")
                    Toast.makeText(this, "Konum alınamadı! Lütfen GPS'inizi kontrol edin.", Toast.LENGTH_LONG).show()
                }
            }
        }

        buttonOnBoard.setOnClickListener {
            markOnBoard(transferId)
        }

        buttonFinishMission.setOnClickListener {
            finishMission(transferId)
        }
    }

    private fun fetchMissionDetails(
        transferId: Int,
        textViewMissionDetail: TextView,
        editTextKm: EditText,
        buttonStartMission: Button,
        btnSurplace: Button,
        buttonOnBoard: Button,
        buttonFinishMission: Button
    ) {
        Log.d("MISSION_DEBUG", "fetchMissionDetails çağrıldı - Transfer ID: $transferId")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@MissionActivity).getAuthToken()
                Log.d("MISSION_DEBUG", "Token alındı: $token")
                val response = ApiClient.apiService.getMissionDetails(token, transferId)
                Log.d("MISSION_DEBUG", "API çağrısı yapıldı - Yanıt kodu: ${response.code()}")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("MISSION_DEBUG", "Başarılı yanıt alındı: ${response.body().toString()}")
                        val missionResponse = response.body()

                        missionResponse?.let {
                            val mission = it.mission
                            val transfer = it.transfert
                            if (transfer == null) {
                                textViewMissionDetail.text = "Transfer bilgisi bulunamadı!"
                                return@withContext
                            }

                            if (mission == null) {
                                // Görev Yoksa: Transfer bilgilerini göster, Başlat butonu aktif
                                textViewMissionDetail.text = """
                                    **TRANSFER BİLGİLERİ**
                                    ID: ${transfer.id}
                                    Nereden: ${transfer.from}
                                    Nereye: ${transfer.target}
                                    Yolcu Sayısı: ${transfer.pax}
                                    Araç: ${transfer.vehicule?.name ?: "Bilinmiyor"}
                                    Ofis Çıkış: ${transfer.ofisStart}
                                    Başlangıç: ${transfer.startDate}
                                """.trimIndent()

                                editTextKm.visibility = View.VISIBLE
                                buttonStartMission.visibility = View.VISIBLE
                                buttonOnBoard.visibility = View.GONE
                                buttonFinishMission.visibility = View.GONE
                            } else {
                                // Görev Varsa: Mevcut görev detaylarını göster
                                textViewMissionDetail.text = """
                                    **GÖREV BİLGİLERİ**
                                    Görev ID: ${mission.id}
                                    Transfer ID: ${mission.transferId}
                                    Başlangıç: ${mission.hareket ?: "Henüz başlamadı"}
                                    Sur Place: ${mission.surplace ?: "Bekleniyor"}
                                    Yolcu Alındı: ${mission.taked ?: "Henüz alınmadı"}
                                    Bitiş: ${mission.finish ?: "Tamamlanmadı"}
                                    Son Kilometre: ${mission.finishKm ?: "-"}
                                    Toplam KM: ${(mission.finishKm ?: 0) - (mission.departKm ?: 0)}
                                """.trimIndent()

                                editTextKm.visibility = View.GONE
                                buttonStartMission.visibility = View.GONE
                                buttonOnBoard.visibility = if (mission.taked == null) View.VISIBLE else View.GONE
                                buttonFinishMission.visibility = if (mission.finish == null) View.VISIBLE else View.GONE
                                btnSurplace.visibility = if (mission.surplace !=null) View.GONE else View.VISIBLE
                            }
                        }
                    } else {
                        Log.e("MISSION_API", "Yanıt Başarısız - Kod: ${response.code()}")
                        textViewMissionDetail.text = "Görev bulunamadı!"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MISSION_API", "Bağlantı hatası: ${e.message}")
                    textViewMissionDetail.text = "Bağlantı hatası oluştu!"
                }
            }
        }
    }

    private fun startMission(transferId: Int, departKm: Int, latitude: Double, longitude: Double) {
        val tag = "START_MISSION"
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@MissionActivity).getAuthToken()
                val request = StartMissionRequest(departKm, latitude, longitude)
                Log.d(tag, "startMission() çağrıldı - Transfer ID: $transferId, KM: $departKm, Lat: $latitude, Long: $longitude")
                val response = ApiClient.apiService.startMission(token, transferId, request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d(tag, "Görev başlatıldı! Yanıt: ${response.body()}")
                        Toast.makeText(this@MissionActivity, "Görev başlatıldı!", Toast.LENGTH_LONG).show()

                        // 🟢 Activity'yi tamamen yeniden yükle
                        recreate()

                    } else {
                        Log.e(tag, "Yanıt Başarısız - Kod: ${response.code()} - Hata: ${response.errorBody()?.string()}")
                        Toast.makeText(this@MissionActivity, "Görev başlatılamadı!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(tag, "Bağlantı hatası: ${e.message}")
                    Toast.makeText(this@MissionActivity, "Bağlantı hatası!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startSurplaceMission(transferId: Int, latitude: Double, longitude: Double) {
        val tag = "START_SURPLACE_MISSION"
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@MissionActivity).getAuthToken()
                val request = mapOf("latitude" to latitude, "longitude" to longitude)  // JSON formatı için Map oluşturuldu

                Log.d(tag, "startSurplaceMission() çağrıldı - Transfer ID: $transferId, Lat: $latitude, Long: $longitude")

                val response = ApiClient.apiService.startSurplaceMission(token, transferId, request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d(tag, "Surplace Görev Başlatıldı! Yanıt: ${response.body()}")
                        Toast.makeText(this@MissionActivity, "Surplace Görev Başlatıldı!", Toast.LENGTH_LONG).show()
                    } else {
                        Log.e(tag, "Yanıt Başarısız - Kod: ${response.code()} - Hata: ${response.errorBody()?.string()}")
                        Toast.makeText(this@MissionActivity, "Surplace Görev Başlatılamadı!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(tag, "Bağlantı Hatası: ${e.message}")
                    Toast.makeText(this@MissionActivity, "Bağlantı Hatası!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }





    private fun markOnBoard(transferId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@MissionActivity).getAuthToken()
                val response = ApiClient.apiService.markOnBoard(token, transferId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MissionActivity, "Yolcu alındı olarak işaretlendi!", Toast.LENGTH_LONG).show()
                        recreate()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MISSION_API", "Bağlantı hatası: ${e.message}")
                }
            }
        }
    }

    private fun finishMission(transferId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@MissionActivity).getAuthToken()
                val response = ApiClient.apiService.finishMission(token, transferId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MissionActivity, "Görev tamamlandı!", Toast.LENGTH_LONG).show()
                        recreate()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MISSION_API", "Bağlantı hatası: ${e.message}")
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(callback: (Double?, Double?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            callback(null, null)
            return
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
                Log.d("GPS_DEBUG", "Konum Alındı: Lat=$latitude, Long=$longitude")
                callback(latitude, longitude)
                locationManager.removeUpdates(this) // Güncelleme durduruluyor
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                Log.e("GPS_DEBUG", "GPS Kapalı!")
                callback(null, null)
            }
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

}
