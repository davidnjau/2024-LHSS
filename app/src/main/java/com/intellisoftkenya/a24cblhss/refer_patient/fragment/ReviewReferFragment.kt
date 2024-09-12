package com.intellisoftkenya.a24cblhss.refer_patient.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentReviewReferBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DbClasses
import com.intellisoftkenya.a24cblhss.dynamic_components.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormData
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ReviewReferViewModel
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ReviewReferFragment : Fragment() {

    private var _binding: FragmentReviewReferBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager


    private val viewModel: ReviewReferViewModel by viewModels()
    private lateinit var formatterClass: FormatterClass
    private lateinit var formDataAdapter: FormDataAdapter
    private var formDataList = ArrayList<FormData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReviewReferBinding.inflate(inflater, container, false)

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
            findNavController().navigate(R.id.action_reviewReferFragment_to_patientCardFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gson = Gson()
        val navigationDetails = DbNavigationDetails.REFER_PATIENT.name
        val registrationClassesList = listOf(
            DbClasses.REFERRING_FACILITY_INFO.name,
            DbClasses.REFERRAL_INFO.name,
            DbClasses.CLINICAL_REFERRAL_I.name,
            DbClasses.CLINICAL_REFERRAL_II.name,
            DbClasses.CLINICAL_REFERRAL_III.name,
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