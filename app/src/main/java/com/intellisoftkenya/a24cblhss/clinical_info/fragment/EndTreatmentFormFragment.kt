package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoDetailsViewModel
import com.intellisoftkenya.a24cblhss.databinding.FragmentEndTreatmentFormBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class EndTreatmentFormFragment : Fragment() {


    private var _binding: FragmentEndTreatmentFormBinding? = null
    private val binding get() = _binding!!  // This property will always refer to the latest binding instance

    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass
    private var patientId:String = ""
    private var carePlanId:String = ""
    private lateinit var clinicalViewModel: ClinicalInfoDetailsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEndTreatmentFormBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        navigationActions()
        patientId = formatterClass.getSharedPref("", "patientId")?: ""
        carePlanId = formatterClass.getSharedPref(DbNavigationDetails.CARE_PLAN.name, "carePlanId")?: ""

        val workflowTitles = formatterClass.getWorkflowTitles(DbClasses.END_TREATMENT_FORM.name)
        if (workflowTitles != null){
            binding.tvTitle.text = formatterClass.toSentenceCase(workflowTitles.text)
            binding.imgBtn.setImageResource(workflowTitles.iconId)
        }

        clinicalViewModel =
            ViewModelProvider(
                this,
                ClinicalInfoDetailsViewModel.ClinicalInfoDetailsViewModelFactory(
                    requireActivity().application,
                    patientId
                )
            )[ClinicalInfoDetailsViewModel::class.java]

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
                findNavController().navigate(R.id.action_endTreatmentFormFragment_to_endTreatmentReviewFragment)

                val formData = FormData(
                    DbClasses.END_TREATMENT_FORM.name,
                    addedFields)

                val gson = Gson()
                val json = gson.toJson(formData)

                formatterClass.saveSharedPref(
                    sharedPrefName = DbNavigationDetails.REFER_PATIENT.name,
                    DbClasses.END_TREATMENT_FORM.name,
                    json
                )

            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = listOf(
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Name of Patient", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.DATE_PICKER.name,
                "Date Patient Reported to Facility",
                true
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Our TB Registration No", true,
                InputType.TYPE_CLASS_NUMBER
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Your TB Registration No", true,
                InputType.TYPE_CLASS_NUMBER
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Final Outcome of treatment", true,
                InputType.TYPE_CLASS_TEXT
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Name of Health Facility", true,
                InputType.TYPE_CLASS_TEXT
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Contact Person", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Designation", true,
                InputType.TYPE_CLASS_TEXT
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Email Contact", true,
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            ),
            DbField(
                DbWidgets.RADIO_BUTTON.name,
                "Referral of PTLD",
                true,
                optionList = listOf("Yes", "No")
            )
        )

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

        FormUtils.loadFormData(
            requireContext(),
            binding.rootLayout,
            DbNavigationDetails.REFERRALS.name,
            DbClasses.END_TREATMENT_FORM.name
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}