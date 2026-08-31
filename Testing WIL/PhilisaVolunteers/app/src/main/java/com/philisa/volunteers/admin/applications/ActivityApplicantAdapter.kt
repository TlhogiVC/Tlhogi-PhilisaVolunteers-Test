package com.philisa.volunteers.admin.applications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.databinding.ItemApplicationBinding
import com.philisa.volunteers.utils.DateUtils

/** New file, not in the original 66 — required Adapter for item_application.xml (Figs 76-77). */
class ActivityApplicantAdapter(
    private var items: List<ActivityApplication>,
    private val onClick: (ActivityApplication) -> Unit
) : RecyclerView.Adapter<ActivityApplicantAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemApplicationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = items[position]
        val context = holder.itemView.context

        holder.binding.tvInitial.text = application.volunteerName.take(1).uppercase()
        holder.binding.tvApplicantName.text = application.volunteerName
        holder.binding.tvApplicantSubtitle.text = context.getString(R.string.applied_on, DateUtils.formatDate(application.appliedDate))

        val isConfirmed = application.status == ActivityApplication.STATUS_CONFIRMED
        holder.binding.tvApplicantStatus.text = context.getString(
            if (isConfirmed) R.string.status_confirmed else R.string.status_pending
        )
        holder.binding.tvApplicantStatus.background.setTint(
            context.getColor(if (isConfirmed) R.color.status_success_bg else R.color.status_pending_bg)
        )
        holder.binding.tvApplicantStatus.setTextColor(
            context.getColor(if (isConfirmed) R.color.status_success_text else R.color.status_pending_text)
        )

        holder.binding.root.setOnClickListener { onClick(application) }
    }

    fun submitList(newItems: List<ActivityApplication>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
