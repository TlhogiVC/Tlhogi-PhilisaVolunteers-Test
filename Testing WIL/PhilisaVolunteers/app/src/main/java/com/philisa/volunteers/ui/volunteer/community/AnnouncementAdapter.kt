package com.philisa.volunteers.ui.volunteer.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.data.model.Announcement
import com.philisa.volunteers.databinding.ItemAnnouncementBinding
import com.philisa.volunteers.utils.DateUtils

/** New file, not in the original 66 — required read-only Adapter for item_announcement.xml
 *  on the volunteer Community page (Figs 63-64). The admin management list uses a separate
 *  ManageAnnouncementAdapter since it needs Publish/Edit/Delete controls this one hides. */
class AnnouncementAdapter(
    private var items: List<Announcement>,
    private val onClick: (Announcement) -> Unit
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = items[position]
        holder.binding.tvAnnouncementTitle.text = announcement.title
        holder.binding.tvAnnouncementDate.text = DateUtils.formatDate(announcement.date)
        holder.binding.tvAnnouncementBody.text = announcement.messageBody
        holder.binding.layoutAdminActions.visibility = android.view.View.GONE
        holder.binding.root.setOnClickListener { onClick(announcement) }
    }

    fun submitList(newItems: List<Announcement>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
