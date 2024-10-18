package com.example.cb_dhs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.cb_dhs.databinding.ReferralListItemViewBinding

/** UI Controller helper class to monitor Patient viewmodel and display list of patients. */
class ReferralItemRecyclerViewAdapter(
    private val onItemClicked: (ReferralListViewModel.ReferralItem) -> Unit,
) :
    ListAdapter<ReferralListViewModel.ReferralItem, ReferralItemViewHolder>(ReferralItemDiffCallback()) {

    class ReferralItemDiffCallback : DiffUtil.ItemCallback<ReferralListViewModel.ReferralItem>() {
        override fun areItemsTheSame(
            oldItem: ReferralListViewModel.ReferralItem,
            newItem: ReferralListViewModel.ReferralItem,
        ): Boolean = oldItem.resourceId == newItem.resourceId

        override fun areContentsTheSame(
            oldItem: ReferralListViewModel.ReferralItem,
            newItem: ReferralListViewModel.ReferralItem,
        ): Boolean = oldItem.id == newItem.id && oldItem.risk == newItem.risk
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferralItemViewHolder {
        return ReferralItemViewHolder(
            ReferralListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ReferralItemViewHolder, position: Int) {
        val item = currentList[position]
        holder.bindTo(item, onItemClicked)
    }
}
