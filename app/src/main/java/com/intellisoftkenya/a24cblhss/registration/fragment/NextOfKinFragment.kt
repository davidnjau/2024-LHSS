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
import com.intellisoftkenya.a24cblhss.registration.viewmodel.NextOfKinViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentNextOfKinBinding
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.MainActivityViewModel

class NextOfKinFragment : Fragment() {

    private var _binding: FragmentNextOfKinBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private val viewModel: MainActivityViewModel by viewModels()

    private val viewModelNextOfKin: NextOfKinViewModel by viewModels()

    private val relationShipList = listOf("Wife", "Husband", "Sister", "Brother", "Son", "Daughter", "Parent")
    private lateinit var formatterClass: FormatterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNextOfKinBinding.inflate(inflater, container, false)

        navigationActions()
        formatterClass = FormatterClass(requireContext())

        val workflowTitles = formatterClass.getWorkflowTitles(DbClasses.NEXT_OF_KIN.name)
        if (workflowTitles != null){
            binding.tvTitle.text = formatterClass.toSentenceCase(workflowTitles.text)
            binding.imgBtn.setImageResource(workflowTitles.iconId)
        }

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
            // Call the function to extract form data
            val (addedFields, missingFields) = FormUtils.extractAllFormData(binding.rootLayout)

            if (missingFields.isNotEmpty()){
                var missingText = ""
                missingFields.forEach { missingText += "\n ${it.tag}, " }

                val mandatoryText = "The following are mandatory fields and " +
                        "need to be filled before proceeding: \n" +
                        missingText

                formatterClass.showDialog("Missing Content", mandatoryText)
            }else{

                val telephoneData = addedFields.find { it.tag == "Telephone" }
                if (telephoneData != null) {
                    val textNumber = telephoneData.text
                    val isPhoneValid = formatterClass.getStandardPhoneNumber(textNumber)
                    if (isPhoneValid) {

                        findNavController().navigate(R.id.action_nextOfKinFragment_to_patientRegistrationSummaryFragment)

                        val formData = FormData(
                            DbClasses.NEXT_OF_KIN.name,
                            addedFields)

                        val gson = Gson()
                        val json = gson.toJson(formData)

                        formatterClass.saveSharedPref(
                            sharedPrefName = DbNavigationDetails.PATIENT_REGISTRATION.name,
                            DbClasses.NEXT_OF_KIN.name,
                            json
                        )

                    }else{
                        Toast.makeText(context, "You have provided an invalid phone number", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FieldManager with dependencies (inject via constructor or manually)
        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        val dbFieldList = listOf(
            DbField(
                DbWidgets.RADIO_BUTTON.name,
                "Can we contact your Next of Kin/Relative?",
                true,
                optionList = listOf("Yes", "No")
            ),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Full Name", true,
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            ),
            DbField(
                DbWidgets.SPINNER.name,
                "Relationship", true, null,
                relationShipList),
            DbField(
                DbWidgets.EDIT_TEXT.name,
                "Telephone", true,
                android.text.InputType.TYPE_CLASS_NUMBER
            )
        )

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

        FormUtils.loadFormData(
            requireContext(),
            binding.rootLayout,
            DbNavigationDetails.PATIENT_REGISTRATION.name,
            DbClasses.NEXT_OF_KIN.name
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}