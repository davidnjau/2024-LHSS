package com.intellisoftkenya.a24cblhss.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.dynamic_components.DbFormData
import com.intellisoftkenya.a24cblhss.dynamic_components.FormData

class FormDataAdapter(
    private val formDataList: ArrayList<FormData>)
    : RecyclerView.Adapter<FormDataAdapter.FormDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_form_data, parent, false)
        return FormDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormDataViewHolder, position: Int) {
        val formData = formDataList[position]
        holder.bind(formData)
    }

    override fun getItemCount(): Int {
        return formDataList.size
    }

    inner class FormDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val contentLayout: LinearLayout = itemView.findViewById(R.id.contentLayout)

        fun bind(formData: FormData) {
            titleTextView.text = formData.title

            // Handle expand/collapse
            titleTextView.setOnClickListener {
                if (contentLayout.visibility == View.GONE) {
                    contentLayout.visibility = View.VISIBLE
                    populateContent(formData.formDataList)
                } else {
                    contentLayout.visibility = View.GONE
                }
            }
        }

        private fun populateContent(dbFormDataList: ArrayList<DbFormData>) {
            contentLayout.removeAllViews() // Clear previous content

            for (dbFormData in dbFormDataList) {
                val dbItemView = LayoutInflater.from(contentLayout.context).inflate(
                    R.layout.list_item_db_form_data, contentLayout, false)

                val tagTextView: TextView = dbItemView.findViewById(R.id.tagTextView)
                val textTextView: TextView = dbItemView.findViewById(R.id.textTextView)

                tagTextView.text = dbFormData.tag
                textTextView.text = dbFormData.text

                contentLayout.addView(dbItemView)
            }
        }
    }
}

