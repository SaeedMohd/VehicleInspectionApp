package com.inspection.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inspection.R
import com.inspection.model.StagedCertStatus
import com.inspection.model.StagedCertificateEntry

class StagedCertificatesAdapter(
    private val items: ArrayList<StagedCertificateEntry>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<StagedCertificatesAdapter.ViewHolder>() {

    private var isSubmitting = false

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeName: TextView = view.findViewById(R.id.itemCertTypeName)
        val dates: TextView = view.findViewById(R.id.itemCertDates)
        val status: TextView = view.findViewById(R.id.itemCertStatus)
        val deleteBtn: ImageButton = view.findViewById(R.id.itemCertDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staged_certificate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val density = holder.itemView.resources.displayMetrics.density
        val pillRadius = 50f * density

        holder.typeName.text = item.certificationTypeName
        holder.dates.text = "${item.certificationDate} – ${item.expirationDate}"

        fun pillChip(color: Int): GradientDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = pillRadius
            setColor(color)
        }

        when (item.status) {
            StagedCertStatus.PENDING -> {
                holder.status.text = "Pending"
                holder.status.background = pillChip(Color.parseColor("#9E9E9E"))
                holder.status.setTextColor(Color.WHITE)
                holder.dates.text = "${item.certificationDate} – ${item.expirationDate}"
            }
            StagedCertStatus.SAVING -> {
                holder.status.text = "Saving…"
                holder.status.background = pillChip(Color.parseColor("#1976D2"))
                holder.status.setTextColor(Color.WHITE)
                holder.dates.text = "${item.certificationDate} – ${item.expirationDate}"
            }
            StagedCertStatus.SAVED -> {
                holder.status.text = "Saved"
                holder.status.background = pillChip(Color.parseColor("#2E7D32"))
                holder.status.setTextColor(Color.WHITE)
                holder.dates.text = "${item.certificationDate} – ${item.expirationDate}"
            }
            StagedCertStatus.FAILED -> {
                holder.status.text = "Failed"
                holder.status.background = pillChip(Color.parseColor("#D32F2F"))
                holder.status.setTextColor(Color.WHITE)
                val errorPreview = if (item.errorMessage.length > 60)
                    item.errorMessage.substring(0, 60) + "…"
                else
                    item.errorMessage
                holder.dates.text = if (errorPreview.isNotEmpty()) errorPreview
                else "${item.certificationDate} – ${item.expirationDate}"
            }
        }

        val deletable = !isSubmitting && item.status != StagedCertStatus.SAVED
        holder.deleteBtn.isEnabled = deletable
        holder.deleteBtn.alpha = if (deletable) 1.0f else 0.3f
        holder.deleteBtn.setOnClickListener {
            if (deletable) onDeleteClick(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setSubmitting(submitting: Boolean) {
        isSubmitting = submitting
        notifyDataSetChanged()
    }
}
