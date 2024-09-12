package com.intellisoftkenya.a24cblhss.registration.fragment

import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.intellisoftkenya.a24cblhss.registration.viewmodel.DemographicsViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbField
import com.intellisoftkenya.a24cblhss.dynamic_components.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.MainActivityViewModel

class DemographicsFragment : Fragment() {

    private var _binding: FragmentDemographicsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass

    private val viewModel: MainActivityViewModel by viewModels()
    private var identificationTypes = listOf("Passport", "Identification Number", "Birth Certificate Number")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDemographicsBinding.inflate(inflater, container, false)

        navigationActions()

        formatterClass = FormatterClass(requireContext())

        // Observe the LiveData for radio button selection changes
        viewModel.radioSelectedOption.observe(viewLifecycleOwner) { selectedOption ->
            val tag = selectedOption.tag // Tag of the selected radio button
            val text = selectedOption.text // Text of the selected radio button

            for (i in 0 until binding.rootLayout.childCount) {
                val childView = binding.rootLayout.getChildAt(i)

                // You can get the tag or label of the child view. Assuming the tag is set to match the label
                val tagChildView = childView.tag?.toString() // Get the tag or label for the child view

                if (tag == "Date of Birth") {
                    if (text == "Estimate") {
                        // Hide the widget with the label "Select Date of Birth"
                        if (tagChildView == "DOB") {
                            childView.visibility = View.GONE
                        }

                        // Ensure the "Year" and "Month" fields are visible
                        if (tagChildView == "Year" || tagChildView == "Month") {
                            childView.visibility = View.VISIBLE
                        }
                    }

                    if (text == "Accurate") {
                        // Hide the "Year" and "Month" widgets
                        if (tagChildView == "Year" || tagChildView == "Month") {
                            childView.visibility = View.GONE
                        }

                        // Ensure the "Select Date of Birth" field is visible
                        if (tagChildView == "DOB") {
                            childView.visibility = View.VISIBLE
                        }
                    }
                }
            }


            // For debugging or further logic
            Log.e("tag ", "$tag")
            Log.e("text ", "$text")
        }


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
                "Telephone", true,
                InputType.TYPE_CLASS_NUMBER
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "Document Type", true, null,
                identificationTypes
            ),
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
                "DOB", true
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Year", true,
                InputType.TYPE_CLASS_NUMBER // Corrected input type for numeric input
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Month", true,
                InputType.TYPE_CLASS_NUMBER // Corrected input type for numeric input
            )


        )


        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())
        // Call the extractFormData function to attach listeners to RadioGroups
        viewModel.extractFormData(binding.rootLayout)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}