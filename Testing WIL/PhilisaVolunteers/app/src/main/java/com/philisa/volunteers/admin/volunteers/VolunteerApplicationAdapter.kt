package com.philisa.volunteers.admin.volunteers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.databinding.ItemVolunteerBinding
import com.philisa.volunteers.utils.DateUtils

/** New file, not in the original 66 — required Adapter for item_volunteer.xml. Reused by both
 *  ManageVolunteersFragment (Fig 70) and AdminDashboardFragment's Recent Applications list
 *  (Figs 68-69), since both show the same VolunteerApplication data. */
class VolunteerApplicationAdapter(
    private var items: List<VolunteerApplication>,
    private val onClick: (VolunteerApplication) -> Unit
) : RecyclerView.Adapter<VolunteerApplicationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVolunteerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVolunteerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = items[position]
        val context = holder.itemView.context
        holder.binding.tvInitial.text = application.firstName.take(1).uppercase()
        holder.binding.tvVolunteerName.text = application.fullName
        holder.binding.tvVolunteerSubtitle.text = "${application.programmeInterest} · ${application.area}"
        holder.binding.tvVolunteerApplied.text = context.getString(R.string.applied_on, DateUtils.formatDate(application.appliedDate))

        val (label, bg, text) = when (application.status) {
            VolunteerApplication.STATUS_APPROVED -> Triple(context.getString(R.string.status_approved), R.color.status_success_bg, R.color.status_success_text)
            VolunteerApplication.STATUS_REJECTED -> Triple(context.getString(R.string.status_rejected), R.color.status_error_bg, R.color.status_error_text)
            else -> Triple(context.getString(R.string.status_pending), R.color.status_pending_bg, R.color.status_pending_text)
        }
        holder.binding.tvVolunteerStatus.text = label
        holder.binding.tvVolunteerStatus.background.setTint(context.getColor(bg))
        holder.binding.tvVolunteerStatus.setTextColor(context.getColor(text))

        holder.binding.root.setOnClickListener { onClick(application) }
    }

    fun submitList(newItems: List<VolunteerApplication>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
