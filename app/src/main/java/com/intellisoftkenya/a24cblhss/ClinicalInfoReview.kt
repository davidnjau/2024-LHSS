package com.intellisoftkenya.a24cblhss

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoDetailsViewModel
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoReviewBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentEndTreatmentReviewBinding
import com.intellisoftkenya.a24cblhss.refer_patient.viewmodel.ReviewReferViewModel
import com.intellisoftkenya.a24cblhss.shared.BlurBackgroundDialog
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.CarePlan

class ClinicalInfoReview : Fragment() {

    private var _binding: FragmentClinicalInfoReviewBinding? = null
    private val binding get() = _binding!!
    private val viewModelSave: ReviewReferViewModel by viewModels()
    private lateinit var formatterClass: FormatterClass

    private lateinit var formDataAdapter: FormDataAdapter

    private var formDataList = ArrayList<FormData>()

    val navigationDetails = DbNavigationDetails.REFER_PATIENT.name

    private var patientId:String = ""
    private var carePlanId:String = ""
    private lateinit var clinicalViewModel: ClinicalInfoDetailsViewModel
    private var workflowTitles:String = ""

    private var registrationClassesList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentClinicalInfoReviewBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        patientId = formatterClass.getSharedPref("", "patientId")?: ""
        carePlanId = formatterClass.getSharedPref(DbNavigationDetails.CARE_PLAN.name, "carePlanId")?: ""
        workflowTitles = formatterClass.getSharedPref("", "CLINICAL_REFERRAL")?: ""
        registrationClassesList.add(workflowTitles)

        navigationActions()

        clinicalViewModel =
            ViewModelProvider(
                this,
                ClinicalInfoDetailsViewModel.ClinicalInfoDetailsViewModelFactory(
                    requireActivity().application,
                    patientId
                )
            )[ClinicalInfoDetailsViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gson = Gson()

        registrationClassesList.forEach {
            val savedJson = formatterClass.getSharedPref(navigationDetails, it)
            val formDataFromJson = gson.fromJson(savedJson, FormData::class.java)
            formDataList.addAll(listOf(formDataFromJson))
        }

        formDataAdapter = FormDataAdapter(formDataList, requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.recyclerView.adapter = formDataAdapter
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

            submitData()
//            findNavController().navigate(R.id.action_reviewReferFragment_to_patientCardFragment)
        }
    }
    private fun submitData() {

        CoroutineScope(Dispatchers.Main).launch {

            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Submission in progress.")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            var savedResources = ArrayList<String>()

            val job = Job()
            CoroutineScope(Dispatchers.IO + job).launch {

                viewModelSave.createClinicalInfo(
                    formDataList,
                    workflowTitles,
                    clinicalViewModel,
                    CarePlan.CarePlanStatus.ACTIVE
                )

                registrationClassesList.forEach {
                    formatterClass.deleteSharedPref(navigationDetails, it)
                }

            }.join()

            progressDialog.dismiss()

            val blurBackgroundDialog = BlurBackgroundDialog(requireContext(),
                "Form has been saved successfully.",
                this@ClinicalInfoReview,
                R.id.action_clinicalInfoReview_to_patientFileFragment
            )
            blurBackgroundDialog.show()



        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}