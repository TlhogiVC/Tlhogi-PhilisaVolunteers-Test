package com.philisa.volunteers.admin.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.databinding.ItemActivityBinding

/** New file, not in the original 66 — required Adapter for item_activity.xml in its admin
 *  management mode (Fig 72): Publish/Unpublish, Edit and Delete replace the volunteer-facing
 *  Apply/Booked pill logic used by ActivityAdapter. */
class ManageActivityAdapter(
    private var items: List<Activity>,
    private val onTogglePublish: (Activity) -> Unit,
    private val onEdit: (Activity) -> Unit,
    private val onDelete: (Activity) -> Unit
) : RecyclerView.Adapter<ManageActivityAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = items[position]
        val context = holder.itemView.context

        holder.binding.tvProgramme.text = activity.programme.uppercase()
        holder.binding.tvActivityTitle.text = activity.title
        holder.binding.tvActivityDateTime.text = activity.dateTimeLabel
        holder.binding.tvActivityLocation.text = activity.location
        holder.binding.tvActivityRole.text = context.getString(R.string.label_role, activity.volunteerRole)
        holder.binding.tvActivitySpots.text = context.getString(R.string.spots_filled, activity.filledSpots, activity.totalSpots)
        holder.binding.layoutAdminActions.isVisible = true

        val isPublished = activity.status == Activity.STATUS_PUBLISHED
        holder.binding.tvActivityStatus.text = context.getString(
            if (isPublished) R.string.status_published else R.string.status_draft
        )
        holder.binding.tvActivityStatus.background.setTint(
            context.getColor(if (isPublished) R.color.status_success_bg else R.color.status_neutral_bg)
        )
        holder.binding.tvActivityStatus.setTextColor(
            context.getColor(if (isPublished) R.color.status_success_text else R.color.status_neutral_text)
        )
        holder.binding.btnTogglePublish.text = context.getString(
            if (isPublished) R.string.action_unpublish else R.string.action_publish
        )

        holder.binding.btnTogglePublish.setOnClickListener { onTogglePublish(activity) }
        holder.binding.btnEdit.setOnClickListener { onEdit(activity) }
        holder.binding.btnDelete.setOnClickListener { onDelete(activity) }
    }

    fun submitList(newItems: List<Activity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
