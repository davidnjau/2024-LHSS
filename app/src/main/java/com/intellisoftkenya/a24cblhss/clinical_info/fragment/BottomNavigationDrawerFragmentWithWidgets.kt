package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class BottomNavigationDrawerFragmentWithWidgets(
    dbFieldList: List<DbField>, workflowTitles: String)
    : BottomSheetDialogFragment() {

    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass
    private val dbFieldList: List<DbField> by lazy { dbFieldList }
    private val workflowTitles: String by lazy { workflowTitles }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the custom layout (without BottomSheetDialog in XML)
        return inflater.inflate(R.layout.bottom_navigation_drawer_with_widgets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())
        formatterClass = FormatterClass(requireContext())

        // Get references to widgets and handle interactions
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val rootLayout: LinearLayout = view.findViewById(R.id.rootLayout)

        FormUtils.populateView(ArrayList(dbFieldList),
            rootLayout, fieldManager, requireContext())

        btnAdd.setOnClickListener {

            val (addedFields, missingFields) = FormUtils.extractAllFormData(rootLayout)
            if (missingFields.isNotEmpty()){
                var missingText = ""
                missingFields.forEach { missingText += "\n ${it.tag}, " }

                val mandatoryText = "The following are mandatory fields and " +
                        "need to be filled before proceeding: \n" +
                        missingText

                formatterClass.showDialog("Missing Content", mandatoryText)
            }else{

                val formData = FormData(
                    workflowTitles.toString(),
                    addedFields)

                val gson = Gson()
                val json = gson.toJson(formData)

                formatterClass.saveSharedPref(
                    sharedPrefName = DbNavigationDetails.REFER_PATIENT.name,
                    workflowTitles.toString(),
                    json
                )

            }

            dismiss() // Close drawer if necessary
        }

    }
}