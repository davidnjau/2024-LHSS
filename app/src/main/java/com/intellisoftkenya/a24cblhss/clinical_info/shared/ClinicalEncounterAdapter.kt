package com.intellisoftkenya.a24cblhss.clinical_info.shared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.shared.DbEncounter


class ClinicalEncounterAdapter(
    private val context: Context,
    private val parentItemList: ArrayList<DbEncounter>
) : RecyclerView.Adapter<ClinicalEncounterAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvFilledBy: TextView = itemView.findViewById(R.id.tvFilledBy)
        val chipContainer: Chip = itemView.findViewById(R.id.chipContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_item, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = parentItemList[position]

        parentItem.date.let { holder.tvDate.text = it }
        parentItem.filledBy.let { holder.tvFilledBy.text = it }

        // Setup the Child RecyclerView


    }

    override fun getItemCount(): Int = parentItemList.size
}