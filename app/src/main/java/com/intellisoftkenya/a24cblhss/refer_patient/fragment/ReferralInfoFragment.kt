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
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentReferPatientBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentReferralInfoBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbClasses
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormData
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ReferralInfoViewModel
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ReferralInfoFragment : Fragment() {

    private var _binding: FragmentReferralInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private val viewModel: ReferralInfoViewModel by viewModels()
    private var referralReasonList = listOf("Leave", "Leave 2")
    private lateinit var formatterClass: FormatterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReferralInfoBinding.inflate(inflater, container, false)
        navigationActions()
        formatterClass = FormatterClass(requireContext())

        return binding.root

    }
    private fun navigationActions() {
        // Set the next button text to "Continue" and add click listeners
        val navigationButtons = binding.navigationButtons
        navigationButtons.setNextButtonText("Next")

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
                findNavController().navigate(R.id.action_referralInfoFragment_to_clinicalInfoIFragment)

                val formData = FormData(
                    DbClasses.REFERRAL_INFO.name,
                    addedFields)

                val gson = Gson()
                val json = gson.toJson(formData)

                formatterClass.saveSharedPref(
                    sharedPrefName = DbNavigationDetails.REFER_PATIENT.name,
                    DbClasses.REFERRAL_INFO.name,
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
                DbWidgets.SPINNER.name,
                "Reason for Referral", true, null,
                referralReasonList),
            DbField(
                DbWidgets.DATE_PICKER.name,
                "Expected date of return",
                true
            )

        )

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}