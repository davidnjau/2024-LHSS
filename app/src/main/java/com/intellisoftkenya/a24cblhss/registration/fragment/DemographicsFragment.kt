package com.intellisoftkenya.a24cblhss.registration.fragment

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.intellisoftkenya.a24cblhss.registration.viewmodel.DemographicsViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils

class DemographicsFragment : Fragment() {

    private var _binding: FragmentDemographicsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager


    private val viewModel: DemographicsViewModel by viewModels()
    private var identificationTypes = listOf("Passport", "Identification Number", "Birth Certificate Number")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDemographicsBinding.inflate(inflater, container, false)

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
            findNavController().navigate(R.id.action_demographicsFragment_to_addressFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = listOf(
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "First Name", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Middle Name", false,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Last Name", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "NickName", false,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Telephone", false,
                InputType.TYPE_CLASS_PHONE
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "Document Type", true, null,
                identificationTypes),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Document Number", true,
                InputType.TYPE_CLASS_PHONE
            ),
            DbField(
                DbWidgets.RADIO_BUTTON.name,
                "Sex",
                true,
                optionList = listOf("Male", "Female")
            ),
            DbField(
                DbWidgets.RADIO_BUTTON.name,
                "Date of Birth",
                true,
                optionList = listOf("Accurate", "Estimate")
            ),
            DbField(
                DbWidgets.DATE_PICKER.name,
                "Date of Birth",
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