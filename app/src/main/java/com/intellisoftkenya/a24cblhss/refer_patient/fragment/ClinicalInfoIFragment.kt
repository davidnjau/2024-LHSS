package com.intellisoftkenya.a24cblhss.refer_patient.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ClinicalInfoIViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils

class ClinicalInfoIFragment : Fragment() {

    private var _binding: FragmentClinicalInfoIBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private val viewModel: ClinicalInfoIViewModel by viewModels()
    private val tbTypeList = listOf("DSTB","DSTB 2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentClinicalInfoIBinding.inflate(inflater, container, false)

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
            findNavController().navigate(R.id.action_clinicalInfoIFragment_to_clnicalInfoIIFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

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

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}