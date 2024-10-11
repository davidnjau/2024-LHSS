package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.app.Application
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.FhirEngine
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.shared.ClinicalParentAdapter
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIIVBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModel
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ClinicalInfoFormIII_IVFragment : Fragment() {

    private var _binding: FragmentClinicalInfoFormIIIIVBinding? = null
    private lateinit var viewModel: ReferralDetailsViewModel

    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass
    private var patientId:String = ""
    private var serviceRequestId:String = ""
    private var workflowTitles:String = ""
    private val monthList = listOf("1 month", "2 month","3 month", "4 month",
        "5 month", "6 month", "7 month", "8 month", "9 month", "10 month",
        "11 month", "12 month")
    private lateinit var fhirEngine: FhirEngine

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentClinicalInfoFormIIIIVBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        navigationActions()
        patientId = formatterClass.getSharedPref("", "patientId")?: ""
        serviceRequestId = formatterClass.getSharedPref("", "serviceRequestId")?: ""
        workflowTitles = formatterClass.getSharedPref("", "CLINICAL_REFERRAL")?: ""

        if (workflowTitles != ""){
            binding.tvTitle.text = formatterClass.toSentenceCase(workflowTitles!!)
//            binding.imgBtn.setImageResource(workflowTitles)
        }

        val dbFieldList = loadFormData()

        binding.btnAdd.setOnClickListener {
            val bottomNavigationDrawerFragment =
                BottomNavigationDrawerFragmentWithWidgets(dbFieldList,workflowTitles)
            bottomNavigationDrawerFragment.show(parentFragmentManager,
                bottomNavigationDrawerFragment.tag)
        }

        // Create sample data
        val parentItemList = viewModel.getClinicalList()

        // Setup Parent RecyclerView
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.parentRecyclerView.adapter = ClinicalParentAdapter(parentItemList)

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
            )[ReferralDetailsViewModel::class.java]

        viewModel.clinicalLiveData.observe(viewLifecycleOwner) { clinicalList ->
            val adapter = ClinicalParentAdapter(clinicalList)
            binding.parentRecyclerView.adapter = adapter
        }


        return binding.root

    }

    private fun loadFormData(): List<DbField> {
        if (workflowTitles == DbClasses.CLINICAL_REFERRAL_III.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.SPINNER.name,
                    "Month", true, null,
                    monthList),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "GXP Result", false,
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Smear Result", false,
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                ),
                DbField(
                    DbWidgets.DATE_PICKER.name,
                    "Date",
                    true
                ),
            )
            return dbFieldList
        }
        if (workflowTitles == DbClasses.CLINICAL_REFERRAL_IV.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.SPINNER.name,
                    "HIV Status", true, null,
                    listOf("BDQ", "Unknown")
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Result", true, null,
                    listOf("Susceptible", "Unknown")
                ),

            )
            return dbFieldList
        }
        if (workflowTitles == DbClasses.CLINICAL_REFERRAL_V.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.SPINNER.name,
                    "Month", false, null,
                    monthList),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "GXP Result", false,
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Smear Result", false,
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                ),
            )
            return dbFieldList
        }

        return emptyList()
    }


    private fun navigationActions() {
        // Set the next button text to "Continue" and add click listeners
        val navigationButtons = binding.navigationButtons
        navigationButtons.setNextButtonText("Close")

        navigationButtons.setBackButtonClickListener {
            // Handle back button click
            findNavController().navigateUp()
        }

        navigationButtons.setNextButtonClickListener {
            // Handle next button click
            // Navigate to the next fragment or perform any action


        }
    }


}