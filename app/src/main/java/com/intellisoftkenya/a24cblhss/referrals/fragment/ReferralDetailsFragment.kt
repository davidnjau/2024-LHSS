package com.intellisoftkenya.a24cblhss.referrals.fragment

import android.app.Application
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentDemographicsBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentReferralDetailsBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientCardViewModel
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModel
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ReferralDetailsFragment : Fragment() {

    private var _binding: FragmentReferralDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager

    private lateinit var viewModel: ReferralDetailsViewModel
    private lateinit var formatterClass: FormatterClass
    private lateinit var fhirEngine: FhirEngine
    private var patientId:String = ""
    private var serviceRequestId:String = ""
    private lateinit var formDataAdapter: FormDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReferralDetailsBinding.inflate(inflater, container, false)

        formatterClass = FormatterClass(requireContext())

        patientId = formatterClass.getSharedPref("", "patientId") ?: ""
        serviceRequestId = formatterClass.getSharedPref("", "serviceRequestId") ?: ""

        fhirEngine = FhirApplication.fhirEngine(requireContext())

        viewModel =
            ViewModelProvider(
                this,
                ReferralDetailsViewModelFactory(
                    requireContext().applicationContext as Application,
                    fhirEngine,
                    patientId,
                    serviceRequestId
                ),
            )
                .get(ReferralDetailsViewModel::class.java)

        navigationActions()

        return binding.root
    }

    private fun navigationActions() {
        // Set the next button text to "Continue" and add click listeners
        val navigationButtons = binding.navigationButtons
        navigationButtons.setNextButtonText("Receive Patient")

        navigationButtons.setBackButtonClickListener {
            // Handle back button click
            findNavController().navigateUp()
        }

        navigationButtons.setNextButtonClickListener {
            // Handle next button click
            // Navigate to the next fragment or perform any action
            findNavController().navigate(R.id.action_referralDetailsFragment_to_acknoledgementFormFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formDataList = viewModel.getServiceRequest()

        formDataAdapter = FormDataAdapter(formDataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.recyclerView.adapter = formDataAdapter

        val fullName = formatterClass.getNameFields(formDataList)
        binding.tvFullName.text = fullName
        val crossBorderId = "Cross Border Id: ${patientId.substring(0,6)}"
        binding.tvCrossBorderId.text = crossBorderId
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}