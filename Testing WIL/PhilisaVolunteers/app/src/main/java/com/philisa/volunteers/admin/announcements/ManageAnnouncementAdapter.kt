package com.philisa.volunteers.admin.announcements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Announcement
import com.philisa.volunteers.databinding.ItemAnnouncementBinding
import com.philisa.volunteers.utils.DateUtils

/** New file, not in the original 66 — required Adapter for item_announcement.xml in its admin
 *  management mode (Fig 74): Publish/Unpublish/Edit/Delete, unlike the volunteer-facing
 *  read-only AnnouncementAdapter. */
class ManageAnnouncementAdapter(
    private var items: List<Announcement>,
    private val onTogglePublish: (Announcement) -> Unit,
    private val onDelete: (Announcement) -> Unit
) : RecyclerView.Adapter<ManageAnnouncementAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAnnouncementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = items[position]
        val context = holder.itemView.context

        holder.binding.tvAnnouncementTitle.text = announcement.title
        holder.binding.tvAnnouncementDate.text = DateUtils.formatDate(announcement.date)
        holder.binding.tvAnnouncementBody.text = announcement.messageBody
        holder.binding.layoutAdminActions.isVisible = true

        val isPublished = announcement.status == Announcement.STATUS_PUBLISHED
        holder.binding.btnTogglePublish.text = context.getString(
            if (isPublished) R.string.action_unpublish else R.string.action_publish
        )
        holder.binding.btnEdit.isVisible = false // Fig 74 has no dedicated edit screen; publish/delete cover the flow
        holder.binding.btnTogglePublish.setOnClickListener { onTogglePublish(announcement) }
        holder.binding.btnDelete.setOnClickListener { onDelete(announcement) }
    }

    fun submitList(newItems: List<Announcement>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
