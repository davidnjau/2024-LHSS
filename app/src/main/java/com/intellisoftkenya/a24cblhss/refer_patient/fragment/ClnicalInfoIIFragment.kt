package com.intellisoftkenya.a24cblhss.refer_patient.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ClnicalInfoIIViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentClnicalInfoIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils

class ClnicalInfoIIFragment : Fragment() {

    private var _binding: FragmentClnicalInfoIIBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager




    private val viewModel: ClnicalInfoIIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClnicalInfoIIBinding.inflate(inflater, container, false)

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
            findNavController().navigate(R.id.action_clnicalInfoIIFragment_to_clinicalInfoIIIFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = listOf(
            DbField(
                DbWidgets.SPINNER.name,
                "HIV Status", true, null,
                listOf("Negative", "Positive")
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "ART Regimen", true, null,
                listOf("ART Regimen")
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "CD4 Count", true, null,
                listOf("CD4 Count")
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "Viral Load", true, null,
                listOf("Viral Load")
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
                listOf("Regimen")
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

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}