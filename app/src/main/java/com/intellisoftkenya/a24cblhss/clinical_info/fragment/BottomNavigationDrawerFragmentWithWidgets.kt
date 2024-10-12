package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoDetailsViewModel
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ReviewReferViewModel
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class BottomNavigationDrawerFragmentWithWidgets(
    dbFieldList: List<DbField>, workflowTitles: String)
    : BottomSheetDialogFragment() {

    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass
    private val dbFieldList: List<DbField> by lazy { dbFieldList }
    private val workflowTitles: String by lazy { workflowTitles }
    private val viewModel: ReviewReferViewModel by viewModels()
    private lateinit var clinicalViewModel: ClinicalInfoDetailsViewModel
    private var patientId:String = ""

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
        patientId = formatterClass.getSharedPref("", "patientId") ?: ""

        clinicalViewModel =
            ViewModelProvider(
                this,
                ClinicalInfoDetailsViewModel.ClinicalInfoDetailsViewModelFactory(
                    requireActivity().application,
                    patientId
                )
            )[ClinicalInfoDetailsViewModel::class.java]

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

                val formDataList = ArrayList<FormData>()
                val formData = FormData(
                    workflowTitles.toString(),
                    addedFields)
                formDataList.add(formData)

                savedData(formDataList)



            }

            dismiss() // Close drawer if necessary
        }

    }

    private fun savedData(formDataList: ArrayList<FormData>) {

        viewModel.createClinicalInfo(formDataList, workflowTitles, clinicalViewModel)

    }
}