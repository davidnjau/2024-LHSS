package com.intellisoftkenya.a24cblhss.clinical_info.shared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.shared.DbCarePlan
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbEncounter


class PatientFileAdapter(
    private val context: Context,
    private val fragment: Fragment,
    private val dbCarePlanList: ArrayList<DbCarePlan>
) : RecyclerView.Adapter<PatientFileAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val tvCreated: TextView = itemView.findViewById(R.id.tvCreated)
        val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        val linear: LinearLayout = itemView.findViewById(R.id.linear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.patient_file_card_details, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = dbCarePlanList[position]

        val currentNo = "Patient File ${position + 1}"
        val status = parentItem.status
        val dateCreated = "Created ${parentItem.dateCreated}"

        parentItem.fileNumber.let { holder.tvFileName.text = currentNo }
        parentItem.dateCreated.let { holder.tvCreated.text = dateCreated }

        var statusValue = ""
        if (status == "ACTIVE") {
            statusValue = "Ongoing"
            holder.chipStatus.setChipBackgroundColorResource(R.color.white)
            holder.chipStatus.setTextColor(context.resources.getColor(R.color.colorPrimary))
            //Change the linear background color and text color based on the status
            holder.linear.setBackgroundResource(R.drawable.patient_file_border_ongoing)
            holder.tvFileName.setTextColor(context.resources.getColor(R.color.white))
            holder.tvCreated.setTextColor(context.resources.getColor(R.color.white))
        }
        if (status == "COMPLETED") {
            statusValue = "Closed"
            holder.chipStatus.setChipBackgroundColorResource(R.color.red)
            holder.chipStatus.setTextColor(context.resources.getColor(R.color.white))
            //Change the linear background color and text color based on the status
            holder.linear.setBackgroundResource(R.drawable.patient_file_border)
            holder.tvFileName.setTextColor(context.resources.getColor(R.color.black))
            holder.tvCreated.setTextColor(context.resources.getColor(R.color.black))
        }


        holder.chipStatus.text = statusValue

        // Add any additional data to the chip if needed
        // Example: holder.chipContainer.text = parentItem.additionalData

    }

    override fun getItemCount(): Int = dbCarePlanList.size
}