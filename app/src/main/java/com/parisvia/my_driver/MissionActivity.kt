package com.parisvia.my_driver

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.parisvia.my_driver.model.DepotFinishMissionRequest
import com.parisvia.my_driver.model.StartMissionRequest
import com.parisvia.my_driver.model.Vehicule
import com.parisvia.my_driver.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val textViewTransferDetail = findViewById<TextView>(R.id.textViewTransferDetail)
        val editTextKm = findViewById<EditText>(R.id.editTextKm)
        val buttonStartMission = findViewById<Button>(R.id.buttonStartMission)
        val buttonOnBoard = findViewById<Button>(R.id.buttonOnBoard)
        val buttonFinishMission = findViewById<Button>(R.id.buttonFinishMission)
        val btnSurplace = findViewById<Button>(R.id.btnSurplace)
        val buttonFisnishDepot =findViewById<Button>(R.id.buttonFisnishDepot)
        val textFrom= findViewById<TextView>(R.id.editfrom)
        val vehicule = findViewById<TextView>(R.id.textViewVehicule)
        val goto = findViewById<TextView>(R.id.textViewgoTO)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val transferId = intent.getIntExtra("transfer_id", -1)
        Log.d("DEBUG_TRANSFER", "Gönderilen Transfer ID: $transferId")
        if (transferId != -1) {
            fetchMissionDetails(
                transferId,
                textViewTransferDetail,
                textViewMissionDetail,
                editTextKm,
                buttonStartMission,
                btnSurplace,
                buttonOnBoard,
                buttonFinishMission,
                buttonFisnishDepot,
                textFrom,
                vehicule,
                goto
            )
        } else {
            textViewMissionDetail.text = "Görev bulunamadı!"
            buttonStartMission.visibility = View.GONE
            buttonOnBoard.visibility = View.GONE
            buttonFinishMission.visibility = View.GONE
            buttonFisnishDepot.visibility=View.GONE

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
        buttonFisnishDepot.setOnClickListener {
            val editTextFinishKm = findViewById<EditText>(R.id.editTextFinishKm)
            val spinnerCleaningStatus = findViewById<Spinner>(R.id.spinnerCleaningStatus)
            val textViewCleaningStatus = findViewById<TextView>(R.id.textViewCleaningStatus)


            // Bileşenleri görünür yap
            editTextFinishKm.visibility = View.VISIBLE
            spinnerCleaningStatus.visibility = View.VISIBLE
            textViewCleaningStatus.visibility = View.VISIBLE

            // Spinner için seçenekleri tanımla
            val cleaningOptions = arrayOf("clean", "dirty")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cleaningOptions)
            spinnerCleaningStatus.adapter = adapter

            buttonFisnishDepot.text = "Onayla"

            // Onayladıktan sonra işlemi tamamla
            buttonFisnishDepot.setOnClickListener {
                val finishKm = editTextFinishKm.text.toString().toIntOrNull()
                val cleaningStatus = spinnerCleaningStatus.selectedItem.toString()

                if (finishKm != null) {
                    depotfinishMission(transferId, finishKm, cleaningStatus)
                } else {
                    Toast.makeText(this, "Lütfen geçerli bir kilometre girin!", Toast.LENGTH_LONG).show()
                }
            }
        }


    }
////////////////////////////ana commut
    private fun fetchMissionDetails(
        transferId: Int,
        textViewTransferDetail: TextView,
        textViewMissionDetail: TextView,
        editTextKm: EditText,
        buttonStartMission: Button,
        btnSurplace: Button,
        buttonOnBoard: Button,
        buttonFinishMission: Button,
        buttonFisnishDepot: Button,
        textFrom: TextView,
        vehicule: TextView,
        goto: TextView

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
                                textViewTransferDetail.text = "Transfer bilgisi bulunamadı!"
                                return@withContext
                            }
                            textViewTransferDetail.text = """
                            Detail Trf: : ${transfer.id} ${transfer.serviceType} """.trimIndent()
                            textFrom.text="${transfer.pecsurplace}  ${transfer.from}"
                            vehicule.text=transfer.vehicule

                            if (mission == null) {


                                editTextKm.visibility = View.VISIBLE
                                buttonStartMission.visibility = View.VISIBLE
                                buttonOnBoard.visibility = View.GONE
                                buttonFinishMission.visibility = View.GONE
                                btnSurplace.visibility =View.GONE
                                goto.visibility=View.GONE
                            } else {
                                // Görev Varsa: Mevcut görev detaylarını göster
                                textViewMissionDetail.text = """
                                    **DETAIL DE MISSION**
                                   
                                    Depart Depot: ${mission.hareket?.substring(11, 16) ?: "Pas encore"}
                                    Sur Place: ${mission.surplace?.substring(11, 16) ?: "En attente"}
                                    PEC: ${mission.taked?.substring(11, 16) ?: "En Attente"}
                                    Depose du client: ${mission.finish ?: "En Attente"}
                                    Retour au Depot: ${mission.finishDepot?: "En Attente"} 
                                    Kilometre a la Fin: ${mission.finishKm ?: "-"}
                                    Total KM: ${(mission.finishKm ?: 0) - (mission.departKm ?: 0)}
                                """.trimIndent()

                                editTextKm.visibility = View.GONE
                                buttonStartMission.visibility = View.GONE

                               if (mission.surplace !=null)
                               {
                                   btnSurplace.visibility = View.GONE
                                   goto.text="Avertir le client et l'agence que vous etes sur place!"
                               }
                               else {
                                   btnSurplace.visibility =  View.VISIBLE
                                   goto.text="Maintenant aller a la zone de depart:   ${transfer.from}"
                            }

                                buttonOnBoard.visibility = if (mission.taked == null && mission.surplace!=null ) View.VISIBLE else View.GONE

                                if (mission.taked!=null ){


                                    if (!transfer.guzergah.isNullOrEmpty())  {
                                        val guzergahList = transfer.guzergah // List<Guzergah>

                                        val guzergahText =guzergahList.joinToString(separator = "\n") { "${it.id} - ${it.details}" }

                                        goto.text = "Veuiller Suivre le Trajets:${guzergahText}"
                                    }
                                buttonFinishMission.visibility =  View.VISIBLE
                                }
                                else {
                                    buttonFinishMission.visibility =View.GONE
                                }

                                buttonFisnishDepot.visibility= if (mission.finishDepot == null && mission.finish!=null)   View.VISIBLE else View.GONE
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
                        recreate()
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
    private fun depotfinishMission(transferId: Int, finishKm: Int, cleaningStatus: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = "Bearer " + SecurePreferences(this@MissionActivity).getAuthToken()

                // API'nin beklediği JSON formatına uygun request objesi oluştur
                val requestBody = DepotFinishMissionRequest(
                    finish_km = finishKm,
                    cleaningStatus = if (cleaningStatus == "clean") 1 else 0
                )

                val response = ApiClient.apiService.finishMissionDepot(token, transferId, requestBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MissionActivity, "Fin de Mission?", Toast.LENGTH_LONG).show()
                        recreate()
                    } else {
                        Log.e("MISSION_API", "Yanıt Başarısız - Kod: ${response.code()} - Hata: ${response.errorBody()?.string()}")
                        Toast.makeText(this@MissionActivity, "İşlem başarısız!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MISSION_API", "Bağlantı finih hatası: ${e.localizedMessage}", e)
                    Toast.makeText(this@MissionActivity, "Bağlantı hatası depot finih!", Toast.LENGTH_LONG).show()
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
