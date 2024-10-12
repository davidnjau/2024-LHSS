package com.intellisoftkenya.a24cblhss.clinical_info.shared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbEncounter


class ClinicalEncounterAdapter(
    private val context: Context,
    private val fragment: Fragment,
    private val clinicalInfo: String?,
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
        parentItem.filledBy.let { holder.tvFilledBy.text = "Filled By:" }

        //Add an icon to the chip based on the clinical info
        holder.chipContainer.setChipIconResource(R.drawable.ic_action_view)


        //Create a setOnClickListener for the chip
        holder.chipContainer.setOnClickListener {

        }

        // Add any additional data to the chip if needed
        // Example: holder.chipContainer.text = parentItem.additionalData

    }

    override fun getItemCount(): Int = parentItemList.size
}