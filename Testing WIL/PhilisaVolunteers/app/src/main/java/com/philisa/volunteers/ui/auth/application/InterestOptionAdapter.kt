package com.philisa.volunteers.ui.auth.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.ItemInterestOptionBinding

/**
 * New file, not in the original 66 — every RecyclerView needs an Adapter and none were listed
 * in the approved file structure. Placed alongside ProgrammeInterestActivity, the only screen
 * that uses item_interest_option.xml (Fig 52).
 */
class InterestOptionAdapter(
    private val options: List<String>,
    private var selectedIndex: Int = -1,
    private val onSelected: (String) -> Unit
) : RecyclerView.Adapter<InterestOptionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemInterestOptionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInterestOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val label = options[position]
        holder.binding.tvInterestLabel.text = label
        val isSelected = position == selectedIndex
        holder.binding.ivCheck.isVisible = isSelected
        holder.binding.cardInterest.setStrokeColor(
            holder.itemView.context.getColor(if (isSelected) R.color.purple_700 else R.color.divider)
        )
        holder.binding.root.setOnClickListener {
            val previous = selectedIndex
            selectedIndex = position
            notifyItemChanged(previous)
            notifyItemChanged(selectedIndex)
            onSelected(label)
        }
    }

    override fun getItemCount(): Int = options.size
}
