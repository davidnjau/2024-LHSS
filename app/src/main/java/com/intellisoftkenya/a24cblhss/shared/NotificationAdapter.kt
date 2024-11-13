package com.intellisoftkenya.a24cblhss.shared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbEncounter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass


class NotificationAdapter(
    private val context: Context,
    private val dbCommunicationDataList: ArrayList<DbCommunicationData>,
    private val fragment: Fragment,
    ) : RecyclerView.Adapter<NotificationAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBtnUnread: ImageButton = itemView.findViewById(R.id.imgBtnUnread)
        val tvNotificationTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val tvNotificationDateTime: TextView = itemView.findViewById(R.id.tvNotificationDateTime)
        val tvNotificationContent: TextView = itemView.findViewById(R.id.tvNotificationContent)
        val tvNotificationViewForm: TextView = itemView.findViewById(R.id.tvNotificationViewForm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_list_view, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = dbCommunicationDataList[position]

        val formatterClass = FormatterClass(context)
        val navigationId = parentItem.navigationId
        val status = parentItem.status
        val basedOn = parentItem.basedOn

        parentItem.title.let { holder.tvNotificationTitle.text = it }
        parentItem.dateTime.let { holder.tvNotificationDateTime.text = it }
        parentItem.content.let { holder.tvNotificationContent.text = it }

        if (status.contains("Completed")) {
            holder.imgBtnUnread.visibility = View.GONE
        }

        holder.tvNotificationViewForm.setOnClickListener {
            formatterClass.saveSharedPref("","notificationBasedOn",basedOn)
            findNavController(fragment).navigate(R.id.action_notificationFragment_to_viewFormFragment)
        }

    }

    override fun getItemCount(): Int = dbCommunicationDataList.size
}