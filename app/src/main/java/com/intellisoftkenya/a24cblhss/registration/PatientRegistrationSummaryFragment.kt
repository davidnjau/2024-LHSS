package com.intellisoftkenya.a24cblhss.registration

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentPatientRegistrationSummaryBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbClasses
import com.intellisoftkenya.a24cblhss.dynamic_components.DbFormData
import com.intellisoftkenya.a24cblhss.dynamic_components.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormData
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.MainActivityViewModel

class PatientRegistrationSummaryFragment : Fragment() {
    private var _binding: FragmentPatientRegistrationSummaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var formatterClass: FormatterClass

    private val viewModelPatientSummary: PatientRegistrationSummaryViewModel by viewModels()

    private var formDataList = ArrayList<FormData>()
    private lateinit var formDataAdapter: FormDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPatientRegistrationSummaryBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this,
            SavedStateViewModelFactory(requireActivity().application, this))
            .get(MainActivityViewModel::class.java)

        navigationActions()

        formatterClass = FormatterClass(requireContext())

        return binding.root
    }
    private fun navigationActions() {
        // Set the next button text to "Continue" and add click listeners
        val navigationButtons = binding.navigationButtons
        navigationButtons.setNextButtonText("Submit")

        navigationButtons.setBackButtonClickListener {
            // Handle back button click
            findNavController().navigateUp()
        }

        navigationButtons.setNextButtonClickListener {
            // Handle next button click
            // Navigate to the next fragment or perform any action



            findNavController().navigate(R.id.action_patientRegistrationSummaryFragment_to_patientCardFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val gson = Gson()

        val navigationDetails = DbNavigationDetails.PATIENT_REGISTRATION.name
        val registrationClassesList = listOf(
            DbClasses.DEMOGRAPHICS.name,
            DbClasses.ADDRESS.name,
            DbClasses.NEXT_OF_KIN.name,
        )
        registrationClassesList.forEach {
            val savedJson = formatterClass.getSharedPref(navigationDetails, it)
            val formDataFromJson = gson.fromJson(savedJson, FormData::class.java)
            formDataList.addAll(listOf(formDataFromJson))
        }

        formDataAdapter = FormDataAdapter(formDataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.recyclerView.adapter = formDataAdapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}