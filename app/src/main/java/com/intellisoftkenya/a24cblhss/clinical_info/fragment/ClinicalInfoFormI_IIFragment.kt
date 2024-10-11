package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.fhir.FhirEngine
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentAcknoledgementDetailsBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentEndTreatmentFormBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
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
    private val tbTypeList = listOf("DSTB, DRTB","PTB , EPTB")

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
            binding.tvTitle.text = formatterClass.toSentenceCase(workflowTitles!!)
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
                    R.id.action_endTreatmentFormFragment_to_endTreatmentReviewFragment)

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
        if (workflowTitles == DbClasses.CLINICAL_REFERRAL_I.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "TB Registration Number", true,
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                ),
                DbField(
                    DbWidgets.DATE_PICKER.name,
                    "TB Registration Date",
                    true
                ),
                DbField(
                    DbWidgets.RADIO_BUTTON.name,
                    "Type of Patient",
                    true,
                    optionList = listOf("New", "Previously Treated")
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Types of TB", true, null,
                    tbTypeList)
            )
            return dbFieldList
        }
        if (workflowTitles == DbClasses.CLINICAL_REFERRAL_II.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.SPINNER.name,
                    "HIV Status", true, null,
                    listOf("Negative", "Positive", "Unknown")
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "ART Regimen", true,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "CD4 Count", true,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Viral Load", true,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Radiological information", true,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.DATE_PICKER.name,
                    "Tb Treatment initiation date",
                    true
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Regimen", true, null,
                    listOf("2RHZE/4RH", "2RHZE/1ORH", "2RHZE/2RH")
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Drugs issued for", true, null,
                    listOf("3 months","6 months")
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Month of Treatment", true, null,
                    listOf("Jan", "Feb", "March")
                )

            )
            return dbFieldList
        }

        return emptyList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = loadFormData()

        FormUtils.populateView(ArrayList(dbFieldList),
            binding.rootLayout, fieldManager, requireContext())

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