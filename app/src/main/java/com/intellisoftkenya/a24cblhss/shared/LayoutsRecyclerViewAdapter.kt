package com.intellisoftkenya.a24cblhss.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intellisoftkenya.a24cblhss.databinding.LandingPageItemBinding

class LayoutsRecyclerViewAdapter(
    private val onItemClick: (LayoutListViewModel.Layout) -> Unit
) :
    ListAdapter<LayoutListViewModel.Layout, LayoutViewHolder>(LayoutDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutViewHolder {
        return LayoutViewHolder(
            LandingPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick,
        )
    }

    override fun onBindViewHolder(holder: LayoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class LayoutViewHolder(
    val binding: LandingPageItemBinding,
    private val onItemClick: (LayoutListViewModel.Layout) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val referralNumbers = FormatterClass(binding.root.context)
        .getSharedPref("","referralNumbers")
    private val notificationSize = FormatterClass(binding.root.context)
        .getSharedPref("","notificationSize")
    fun bind(layout: LayoutListViewModel.Layout) {
        binding.componentLayoutIconImageview.setImageResource(layout.iconId)
        binding.componentLayoutTextView.text = layout.textId
        binding.root.setOnClickListener { onItemClick(layout) }

        if (layout.textId == "Referrals") {
            binding.tvNumber.visibility = View.VISIBLE
            binding.tvNumber.text = referralNumbers
        }
        if (layout.textId == "Notifications") {
            binding.tvNumber.visibility = View.VISIBLE
            binding.tvNumber.text = notificationSize
        }

    }
}

class LayoutDiffUtil : DiffUtil.ItemCallback<LayoutListViewModel.Layout>() {
    override fun areItemsTheSame(
        oldLayout: LayoutListViewModel.Layout,
        newLayout: LayoutListViewModel.Layout,
    ) = oldLayout === newLayout

    override fun areContentsTheSame(
        oldLayout: LayoutListViewModel.Layout,
        newLayout: LayoutListViewModel.Layout,
    ) = oldLayout == newLayout
}