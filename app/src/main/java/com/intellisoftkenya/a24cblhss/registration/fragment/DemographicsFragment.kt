package com.intellisoftkenya.a24cblhss.registration.fragment

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
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils.extractAllFormData
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

        binding.tvTitle.text = formatterClass.toSentenceCase(DbClasses.DEMOGRAPHICS.name)


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

            // Call the function to extract form data
            val (addedFields, missingFields) = extractAllFormData(binding.rootLayout)

            if (missingFields.isNotEmpty()){
                Toast.makeText(context, "Please fill all mandatory fields", Toast.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_demographicsFragment_to_addressFragment)

                val formData = FormData(
                    DbClasses.DEMOGRAPHICS.name,
                    addedFields)

                val gson = Gson()
                val json = gson.toJson(formData)

                formatterClass.saveSharedPref(
                    sharedPrefName = DbNavigationDetails.PATIENT_REGISTRATION.name,
                    DbClasses.DEMOGRAPHICS.name,
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
                "DOB", false
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Year", false,
                InputType.TYPE_CLASS_NUMBER // Corrected input type for numeric input
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Month", false,
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