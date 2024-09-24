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
import com.intellisoftkenya.a24cblhss.databinding.FragmentAddressBinding
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.registration.viewmodel.AddressViewModel
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.MainActivityViewModel

class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressViewModel by viewModels()

    private lateinit var fieldManager: FieldManager
    private var countryOriginList = listOf(
        "Kenya", "Uganda")

    private lateinit var formatterClass: FormatterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddressBinding.inflate(inflater, container, false)

        navigationActions()
        formatterClass = FormatterClass(requireContext())

        // Observe the locations LiveData
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            // Update your UI with the locations
            // For example, populate a RecyclerView or Spinner
            val dbFieldList = listOf(

                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Region/Province/County", true,
                    InputType.TYPE_CLASS_TEXT),

                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Sub County/Districts", true,
                    InputType.TYPE_CLASS_TEXT),

                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Ward/Village", true,
                    InputType.TYPE_CLASS_TEXT
                )

            )
            FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())


        }

        binding.tvTitle.text = formatterClass.toSentenceCase(DbClasses.ADDRESS.name)

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
            val (addedFields, missingFields) = FormUtils.extractAllFormData(binding.rootLayout)

            if (missingFields.isNotEmpty()){
                Toast.makeText(context, "Please fill all mandatory fields", Toast.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_addressFragment_to_nextOfKinFragment)
                val formData = FormData(
                    DbClasses.ADDRESS.name,
                    addedFields)

                val gson = Gson()
                val json = gson.toJson(formData)

                formatterClass.saveSharedPref(
                    sharedPrefName = DbNavigationDetails.PATIENT_REGISTRATION.name,
                    DbClasses.ADDRESS.name,
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
                "Country of Origin", true, null,
                countryOriginList),
            DbField(
                DbWidgets.SPINNER.name,
                "Country of Residence", true, null,
                countryOriginList),
        )

        FormUtils.populateView(ArrayList(dbFieldList), binding.rootLayout, fieldManager, requireContext())

//        viewModel.extractFormData(binding.rootLayout)

        FormUtils.loadFormData(
            requireContext(),
            binding.rootLayout,
            DbNavigationDetails.PATIENT_REGISTRATION.name,
            DbClasses.ADDRESS.name
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}