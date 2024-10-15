package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.app.Application
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.FhirEngine
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.shared.ClinicalChildAdapter
import com.intellisoftkenya.a24cblhss.clinical_info.shared.ClinicalParentAdapter
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoViewViewModel
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIIVBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultSpinnerSelectionHandler
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.dynamic_components.SpinnerSelectionHandler
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    private var carePlanId:String = ""
    private var encounterId:String = ""
    private val clinicalInfoViewViewModel: ClinicalInfoViewViewModel by viewModels()
    private val spinnerSelectionHandler: SpinnerSelectionHandler = DefaultSpinnerSelectionHandler()


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

        carePlanId = formatterClass.getSharedPref(DbNavigationDetails.CARE_PLAN.name,"carePlanId")?: ""
        encounterId = formatterClass.getSharedPref("","encounterId")?: ""

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


        CoroutineScope(Dispatchers.IO).launch {

            val formData = viewModel.getEncounterObservationList(workflowTitles)

            CoroutineScope(Dispatchers.Main).launch {

                binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.parentRecyclerView.adapter = ClinicalParentAdapter(formData)
            }
        }

        startRepeatingTask()

        return binding.root

    }


    fun startRepeatingTask() {
        // Use lifecycleScope to ensure coroutines respect the Activity/Fragment lifecycle
        lifecycleScope.launch {
            while (true) {
                // Invoke the function and get the result
                val formData = viewModel.getEncounterObservationList(workflowTitles)
                binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.parentRecyclerView.adapter = ClinicalParentAdapter(formData)

                // Suspend for 5 seconds before the next iteration
                delay(5000)
            }
        }
    }

    private fun loadFormData(): List<DbField> {
        if (workflowTitles == DbClasses.LABORATORY_RESULTS.name) {
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
        if (workflowTitles == DbClasses.DST.name) {
            val dbFieldList = listOf(
                DbField(
                    DbWidgets.SPINNER.name,
                    "Drug", true, null,
                    listOf("R", "BDQ","H","LZD", "LFX", "MFX", "CFZ","Cs","DLM","ETO","SLIDs","Others, Specify")
                ),
                DbField(
                    DbWidgets.EDIT_TEXT.name,
                    "Specify Others", false,
                    InputType.TYPE_CLASS_TEXT
                ),
                DbField(
                    DbWidgets.SPINNER.name,
                    "Result", true, null,
                    listOf("Resistance", "Susceptible","Not Done","Done Awaiting Results")
                ),

            )
            return dbFieldList
        }
        if (workflowTitles == DbClasses.DR_TB_FOLLOW_UP_TEST.name) {
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

            findNavController().navigateUp()
        }
    }


}