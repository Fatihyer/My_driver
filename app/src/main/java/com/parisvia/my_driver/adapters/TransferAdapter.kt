package com.parisvia.my_driver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parisvia.my_driver.R
import com.parisvia.my_driver.model.Transfer
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.ImageButton

class TransferAdapter(private val transfers: List<Transfer>) : RecyclerView.Adapter<TransferAdapter.TransferViewHolder>() {

    class TransferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      //  val tvPostId: TextView = view.findViewById(R.id.tvPostId)
        val tvSurPlace: TextView = view.findViewById(R.id.tvSurPlace)
        val tvServiceType: TextView = view.findViewById(R.id.tvServiceType)
        val tvFromTo: TextView = view.findViewById(R.id.tvFrom)
        val tvDepot: TextView = view.findViewById(R.id.tvDepot)
        val btnDetail: ImageButton = view.findViewById(R.id.btnDetail)

        //val tvStatus: TextView = view.findViewById(R.id.tvStatus)
     //   val tvPayment: TextView = view.findViewById(R.id.tvPayment)
     //   val tvDetail: TextView = view.findViewById(R.id.tvDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transfer, parent, false)
        return TransferViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        val transfer = transfers[position]

        // Tarihi formatla
        val formattedDate = formatDate(transfer.start_date)
        val formattedDepotDate = formatDate(transfer.ofis_start)
        // UI güncelle
      //  holder.tvPostId.text = "Dosya No: ${transfer.post_id ?: "Belirtilmemiş"}"
        holder.tvDepot.text = formattedDepotDate
        holder.tvSurPlace.text = formattedDate
        holder.tvServiceType.text = "${transfer.servicetype.name ?: "Belirtilmemiş"}"
        holder.tvFromTo.text = transfer.from
        holder.btnDetail.setOnClickListener {
            fetchTransferDetail(holder, transfer.id)
        }

      //  holder.tvStatus.text = "Durum: ${getStatusText(transfer.status_id)}"
      //  holder.tvPayment.text = "Ödeme: ${transfer.comments ?: "Belirtilmemiş"}"
      //  holder.tvDetail.text = "Detay: ${transfer.dcomments ?: "Detay Yok"}"
    }

    override fun getItemCount(): Int {
        return transfers.size
    }
    // Tarih formatını güncelle


    private fun formatDate(dateString: String): String {
        return try {

            Log.d("JSON_DATE", "gelen tarig: $dateString" )

            // ISO 8601 formatındaki tarihi parse etmek için giriş formatı
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // UTC olarak parse et

            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("UTC") // Çıktıyı da UTC formatında tut

            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date()) // Hata durumunda şu anki zamanı döndür
        } catch (e: Exception) {
            dateString // Hata olursa orijinal tarihi döndür
        }
    }


    // Durum metni ekleyelim
    private fun getStatusText(statusId: Int?): String {
        return when (statusId) {
            1 -> "Beklemede"
            2 -> "Onaylandı"
            3 -> "Yolda"
            4 -> "Tamamlandı"
            5 -> "İptal Edildi"
            else -> "Bilinmiyor"
        }
    }
}
