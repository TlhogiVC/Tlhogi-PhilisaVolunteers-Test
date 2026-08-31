package com.philisa.volunteers.ui.volunteer.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.databinding.ItemActivityBinding

/**
 * New file, not in the original 66 — required Adapter for item_activity.xml. Shared by
 * ActivitiesFragment (Apply tab, [applicationStatus] null) and MyActivitiesFragment
 * (Today/Upcoming/Completed tabs, [applicationStatus] set from the matching ActivityApplication).
 */
data class ActivityListItem(val activity: Activity, val applicationStatus: String? = null)

class ActivityAdapter(
    private var items: List<ActivityListItem>,
    private val onClick: (Activity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (activity, applicationStatus) = items[position]
        val context = holder.itemView.context
        holder.binding.tvProgramme.text = activity.programme.uppercase()
        holder.binding.tvActivityTitle.text = activity.title
        holder.binding.tvActivityDateTime.text = activity.dateTimeLabel
        holder.binding.tvActivityLocation.text = activity.location
        holder.binding.tvActivityRole.text = context.getString(R.string.label_role, activity.volunteerRole)

        if (applicationStatus == null) {
            holder.binding.tvActivitySpots.isVisible = true
            holder.binding.tvActivitySpots.text = context.getString(R.string.spots_left, activity.spotsRemaining)
            holder.binding.tvActivityStatus.text = context.getString(R.string.status_open)
            setPillColor(holder, R.color.status_success_bg, R.color.status_success_text)
        } else {
            // Fig 60/61 show a single "Booked" pill regardless of pending/confirmed sub-state —
            // that distinction is only surfaced on the admin side (Figs 76-77).
            holder.binding.tvActivitySpots.isVisible = false
            holder.binding.tvActivityStatus.text = context.getString(R.string.status_booked)
            setPillColor(holder, R.color.status_success_bg, R.color.status_success_text)
        }

        holder.binding.root.setOnClickListener { onClick(activity) }
    }

    private fun setPillColor(holder: ViewHolder, bgRes: Int, textRes: Int) {
        val context = holder.itemView.context
        holder.binding.tvActivityStatus.background.setTint(context.getColor(bgRes))
        holder.binding.tvActivityStatus.setTextColor(context.getColor(textRes))
    }

    fun submitList(newItems: List<ActivityListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
