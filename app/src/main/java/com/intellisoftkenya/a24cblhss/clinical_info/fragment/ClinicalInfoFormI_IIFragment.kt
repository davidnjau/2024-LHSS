package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.fhir.FhirEngine
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoViewViewModel
import com.intellisoftkenya.a24cblhss.databinding.FragmentAcknoledgementDetailsBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentEndTreatmentFormBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.fhir.Constants
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ClinicalInfoFormI_IIFragment : Fragment() {

    private var _binding: FragmentClinicalInfoFormIIIBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager
    private var formDataList = ArrayList<FormData>()
    private lateinit var formDataAdapter: FormDataAdapter
    private lateinit var fhirEngine: FhirEngine
    private lateinit var formatterClass: FormatterClass
    private var patientId:String = ""
    private var serviceRequestId:String = ""
    private var workflowTitles:String = ""
    private val tbTypeList = listOf("PTB" , "EPTB")
    private val monthList = listOf("1 month", "2 month","3 month", "4 month",
        "5 month", "6 month", "7 month", "8 month", "9 month", "10 month",
        "11 month", "12 month")
    private val clinicalInfoViewViewModel: ClinicalInfoViewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentClinicalInfoFormIIIBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        navigationActions()
        patientId = formatterClass.getSharedPref("", "patientId")?: ""
        serviceRequestId = formatterClass.getSharedPref("", "serviceRequestId")?: ""
        workflowTitles = formatterClass.getSharedPref("", "CLINICAL_REFERRAL")?: ""

        if (workflowTitles != ""){
            var title = formatterClass.toSentenceCase(workflowTitles)
            if (workflowTitles == DbClasses.TB_TREATMENT.name){
                title = "Tb Treatment"
            }
            binding.tvTitle.text = title
//            binding.imgBtn.setImageResource(workflowTitles)
        }


        return binding.root
    }

    private fun navigationActions() {
        // Set the next button text to "Continue" and add click listeners
        val navigationButtons = binding.navigationButtons
        navigationButtons.setNextButtonText("Save")

        navigationButtons.setBackButtonClickListener {
            // Handle back button click
            findNavController().navigateUp()
        }

        navigationButtons.setNextButtonClickListener {
            // Handle next button click
            // Navigate to the next fragment or perform any action

            val (addedFields, missingFields) = FormUtils.extractAllFormData(binding.rootLayout)
            if (missingFields.isNotEmpty()){
                var missingText = ""
                missingFields.forEach { missingText += "\n ${it.tag}, " }

                val mandatoryText = "The following are mandatory fields and " +
                        "need to be filled before proceeding: \n" +
                        missingText

                formatterClass.showDialog("Missing Content", mandatoryText)
            }else{
                findNavController().navigate(
                    R.id.action_clinicalInfoFormI_IIFragment_to_clinicalInfoReview)

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
        }
    }

    private fun loadFormData(): List<DbField> {
        if (workflowTitles == DbClasses.TB_TREATMENT.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "TB Registration Number", true,
                    InputType.TYPE_CLASS_TEXT,
                    emptyList(),
                    true,
                    Constants.SYSTEM_TB_REGISTRATION
                ),
                DbField(
                    DbWidgets.DATE_PICKER.name,
                    "TB Registration Date",
                    true
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Type of Patient",
                    true,
                    optionList = listOf("New", "Previously Treated")
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Types of TB", true, null,
                    tbTypeList),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Specify Tb Location Site", false,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Radiological Information (If Applicable)", false,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.DATE_PICKER.name,
                    "Tb Treatment initiation date",
                    true
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Month of Treatment", true, null,
                    monthList
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Regimen", true, null,
                    listOf("2RHZE/4RH", "2RHZE/1ORH", "2RHZE/2RH")
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Drugs issued for (Indicate duration)", false,
                    InputType.TYPE_CLASS_TEXT
                )

            )
            return dbFieldList
        }
        if (workflowTitles == DbClasses.HIV_STATUS_TREATMENT.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.SPINNER.name,
                    "HIV Status", true, null,
                    listOf("Negative", "Positive", "Unknown")
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "ART Regimen", false,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "CD4 Count", false,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Viral Load", false,
                    InputType.TYPE_CLASS_TEXT
                )
            )


            return dbFieldList
        }

        return emptyList()
    }

    fun findTextViewByText(rootLayout: ViewGroup, searchText: String): TextView? {
        for (i in 0 until rootLayout.childCount) {
            val child = rootLayout.getChildAt(i)

            // Check if the child is a TextView
            if (child is TextView) {
                // Compare the text of the TextView
                if (child.text.toString() == searchText) {
                    return child
                }
            }

            // If the child is a ViewGroup (like LinearLayout), recursively search its children
            if (child is ViewGroup) {
                val result = findTextViewByText(child, searchText)
                if (result != null) return result
            }
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = loadFormData()


        FormUtils.populateView(ArrayList(dbFieldList),
            binding.rootLayout, fieldManager, requireContext())

        val rootViewParent = binding.rootLayout.findViewWithTag<View>("HIV Status")
        if (rootViewParent != null) {
            //Check if rootViewParent is EditText and set its text from the retrieved observation
            if (rootViewParent is Spinner) {

                rootViewParent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedItem = parent.getItemAtPosition(position) as String
                        clinicalInfoViewViewModel.updateSelectedItem(selectedItem)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle case where no item is selected if needed
                    }
                }

            }
        }

        // Observe the LiveData and react to changes
        clinicalInfoViewViewModel.selectedItem.observe(viewLifecycleOwner) { selectedItem ->
            // Update UI or perform actions based on selected item
            // For example, updating edit texts based on spinner selection
            val artRegimen = binding.rootLayout.findViewWithTag<View>("ART Regimen")
            val cd4Count = binding.rootLayout.findViewWithTag<View>("CD4 Count")
            val viralLoad = binding.rootLayout.findViewWithTag<View>("Viral Load")

            val artRegimenText = findTextViewByText(binding.rootLayout, "ART Regimen")
            val cd4CountText = findTextViewByText(binding.rootLayout, "CD4 Count")
            val viralLoadText = findTextViewByText(binding.rootLayout, "Viral Load")

            if (selectedItem == "Positive") {
                artRegimen?.visibility = View.VISIBLE
                artRegimenText?.visibility = View.VISIBLE

                cd4Count?.visibility = View.VISIBLE
                cd4CountText?.visibility = View.VISIBLE

                viralLoad?.visibility = View.VISIBLE
                viralLoadText?.visibility = View.VISIBLE
            }else{
                artRegimen?.visibility = View.GONE
                artRegimenText?.visibility = View.GONE

                cd4Count?.visibility = View.GONE
                cd4CountText?.visibility = View.GONE

                viralLoad?.visibility = View.GONE
                viralLoadText?.visibility = View.GONE
            }

        }

        FormUtils.loadFormData(
            requireContext(),
            binding.rootLayout,
            DbNavigationDetails.REFER_PATIENT.name,
            workflowTitles.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}