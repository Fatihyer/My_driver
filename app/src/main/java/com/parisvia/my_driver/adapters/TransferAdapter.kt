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
import android.content.Intent
import com.parisvia.my_driver.TransferDetailActivity





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
            val context = holder.itemView.context
            val intent = Intent(context, TransferDetailActivity::class.java)
            intent.putExtra("transfer_id", transfer.id) // Transfer ID'yi yeni aktiviteye gönder
            context.startActivity(intent)
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
            Log.d("JSON_DATE", "Gelen tarih: $dateString")

            // JSON formatındaki tarihi doğru parse et
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Gelen tarih UTC kabul edilir

            val outputFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
            outputFormat.timeZone = TimeZone.getTimeZone("UTC") // UTC'den UTC'ye çevirerek farkı kaldır

            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date()) // Eğer hata olursa şimdiki zamanı döndür
        } catch (e: Exception) {
            Log.e("JSON_DATE", "Tarih formatlama hatası", e)
            dateString // Hata olursa orijinal tarihi döndür
        }
    }


    private fun logJsonData(jsonString: String) {
        try {
            Log.d("JSON_LOG", "Gelen JSON: $jsonString")
        } catch (e: Exception) {
            Log.e("JSON_LOG", "JSON loglama hatası", e)
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
