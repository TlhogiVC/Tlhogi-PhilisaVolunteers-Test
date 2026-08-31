package com.philisa.volunteers.ui.volunteer.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.databinding.ItemScheduleBinding

/** New file, not in the original 66 — required Adapter for item_schedule.xml (Fig 62). */
class ScheduleAdapter(
    private var items: List<Activity>,
    private val onClick: (Activity) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = items[position]
        holder.binding.tvScheduleTime.text = activity.startTime
        holder.binding.tvScheduleTitle.text = activity.title
        holder.binding.tvScheduleSubtitle.text = "${activity.date} · ${activity.location}"
        holder.binding.root.setOnClickListener { onClick(activity) }
    }

    fun submitList(newItems: List<Activity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
