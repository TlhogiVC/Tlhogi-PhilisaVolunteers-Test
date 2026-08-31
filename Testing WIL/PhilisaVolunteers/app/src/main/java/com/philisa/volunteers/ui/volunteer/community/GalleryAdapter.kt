package com.philisa.volunteers.ui.volunteer.community

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.data.model.GalleryItem
import com.philisa.volunteers.databinding.ItemGalleryBinding

/** New file, not in the original 66 — required read-only Adapter for item_gallery.xml on the
 *  volunteer Community page (Fig 64). Manage Gallery (admin, Fig new-screen) uses its own
 *  ManageGalleryAdapter with the delete button enabled. */
class GalleryAdapter(private var items: List<GalleryItem>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.btnDeletePhoto.visibility = android.view.View.GONE
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .centerCrop()
            .into(holder.binding.ivGalleryPhoto)
    }

    fun submitList(newItems: List<GalleryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
