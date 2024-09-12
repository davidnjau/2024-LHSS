package com.intellisoftkenya.a24cblhss.refer_patient.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ClinicalInfoIIIViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoIIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbClasses
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormData
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ClinicalInfoIIIFragment : Fragment() {

    private var _binding: FragmentClinicalInfoIIIBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private val viewModel: ClinicalInfoIIIViewModel by viewModels()
    private lateinit var formatterClass: FormatterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentClinicalInfoIIIBinding.inflate(inflater, container, false)

        navigationActions()
        formatterClass = FormatterClass(requireContext())

        return binding.root

    }

    private fun navigationActions() {
        // Set the next button text to "Continue" and add click listeners
        val navigationButtons = binding.navigationButtons
        navigationButtons.setNextButtonText("Review")

        navigationButtons.setBackButtonClickListener {
            // Handle back button click
            findNavController().navigateUp()
        }

        navigationButtons.setNextButtonClickListener {
            // Handle next button click
            // Navigate to the next fragment or perform any action

            val (addedFields, missingFields) = FormUtils.extractAllFormData(binding.rootLayout)
            if (missingFields.isNotEmpty()){
                Toast.makeText(context, "Please fill all mandatory fields", Toast.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_clinicalInfoIIIFragment_to_reviewReferFragment)

                val formData = FormData(
                    DbClasses.CLINICAL_REFERRAL_III.name,
                    addedFields)

                val gson = Gson()
                val json = gson.toJson(formData)

                formatterClass.saveSharedPref(
                    sharedPrefName = DbNavigationDetails.REFER_PATIENT.name,
                    DbClasses.CLINICAL_REFERRAL_III.name,
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
                "Pre treatment Results", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.DATE_PICKER.name,
                "Pre treatment Date",
                true
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "2 Months Results", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.DATE_PICKER.name,
                "2 Months Date",
                true
            )
            /**
             * Check in with Muyundo first for this view.
             */



        )

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}