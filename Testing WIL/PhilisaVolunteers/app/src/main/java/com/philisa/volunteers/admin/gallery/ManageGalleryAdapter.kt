package com.philisa.volunteers.admin.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.philisa.volunteers.data.model.GalleryItem
import com.philisa.volunteers.databinding.ItemGalleryBinding

/** New file, not in the original 66 — required Adapter for item_gallery.xml in its admin
 *  management mode (delete button visible, unlike the volunteer-facing read-only GalleryAdapter). */
class ManageGalleryAdapter(
    private var items: List<GalleryItem>,
    private val onDelete: (GalleryItem) -> Unit
) : RecyclerView.Adapter<ManageGalleryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView.context).load(item.imageUrl).centerCrop().into(holder.binding.ivGalleryPhoto)
        holder.binding.btnDeletePhoto.isVisible = true
        holder.binding.btnDeletePhoto.setOnClickListener { onDelete(item) }
    }

    fun submitList(newItems: List<GalleryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
