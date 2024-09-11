package com.intellisoftkenya.a24cblhss.refer_patient.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentReferPatientBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ReferPatientViewModel

class ReferPatientFragment : Fragment() {

    private var _binding: FragmentReferPatientBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private val viewModel: ReferPatientViewModel by viewModels()
    private val countryList = listOf("Kenya", "Uganda")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReferPatientBinding.inflate(inflater, container, false)

        navigationActions()

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
            findNavController().navigate(R.id.action_referPatientFragment_to_referralInfoFragment)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = listOf(
            DbField(
                DbWidgets.DATE_PICKER.name,
                "Date of Referral",
                true
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "Country", true, null,
                countryList),
            DbField(
                DbWidgets.SPINNER.name,
                "Region/Province/County", true, null,
                countryList),
            DbField(
                DbWidgets.SPINNER.name,
                "District/Sub County", true, null,
                countryList),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Name of Referring Officer", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "Title", true, null,
                countryList),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Telephone", true,
                InputType.TYPE_CLASS_PHONE
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Email", true,
                InputType.TYPE_CLASS_PHONE
            )
        )

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}