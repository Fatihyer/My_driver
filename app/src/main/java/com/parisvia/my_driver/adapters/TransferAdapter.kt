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

class TransferAdapter(private val transfers: List<Transfer>) : RecyclerView.Adapter<TransferAdapter.TransferViewHolder>() {

    class TransferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPostId: TextView = view.findViewById(R.id.tvPostId)
        val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val tvServiceType: TextView = view.findViewById(R.id.tvServiceType)
        val tvFrom: TextView = view.findViewById(R.id.tvFrom)
        val tvTo: TextView = view.findViewById(R.id.tvTo)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPayment: TextView = view.findViewById(R.id.tvPayment)
        val tvDetail: TextView = view.findViewById(R.id.tvDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transfer, parent, false)
        return TransferViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        val transfer = transfers[position]

        // Tarihi formatla
        val formattedDate = formatDate(transfer.start_date)

        // UI güncelle
        holder.tvPostId.text = "Dosya No: ${transfer.post_id ?: "Belirtilmemiş"}"
        holder.tvStartDate.text = "Tarih: $formattedDate"
        holder.tvServiceType.text = "Hizmet Türü: ${transfer.servicetype_id ?: "Belirtilmemiş"}"
        holder.tvFrom.text = "Nereden: ${transfer.from}"
        holder.tvTo.text = "Nereye: ${transfer.target}"
        holder.tvStatus.text = "Durum: ${getStatusText(transfer.status_id)}"
        holder.tvPayment.text = "Ödeme: ${transfer.comments ?: "Belirtilmemiş"}"
        holder.tvDetail.text = "Detay: ${transfer.dcomments ?: "Detay Yok"}"
    }

    override fun getItemCount(): Int {
        return transfers.size
    }

    // Tarih formatını güncelle
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString // Eğer hata olursa, orijinal tarihi göster
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
