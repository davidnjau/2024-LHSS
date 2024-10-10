package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.app.ProgressDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.EndTreatmentReviewViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentEndTreatmentReviewBinding
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

class EndTreatmentReviewFragment : Fragment() {

    private var _binding: FragmentEndTreatmentReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EndTreatmentReviewViewModel by viewModels()

    private lateinit var formatterClass: FormatterClass

    private lateinit var formDataAdapter: FormDataAdapter

    private var formDataList = ArrayList<FormData>()

    val navigationDetails = DbNavigationDetails.REFER_PATIENT.name
    val registrationClassesList = listOf(
        DbClasses.END_TREATMENT_FORM.name
    )
    private var patientId:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEndTreatmentReviewBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        patientId = formatterClass.getSharedPref("", "patientId")?: ""

        navigationActions()

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
            progressDialog.setMessage("Referral in progress.")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            var savedResources = ArrayList<String>()

            val job = Job()
            CoroutineScope(Dispatchers.IO + job).launch {

//                savedResources = ArrayList(viewModel.createServiceRequest(
//                    formDataList, patientId, null))

                registrationClassesList.forEach {
                    formatterClass.deleteSharedPref(navigationDetails, it)
                }

            }.join()

            progressDialog.dismiss()

            val blurBackgroundDialog = if (savedResources.isNotEmpty()){
                //Save was okay
                BlurBackgroundDialog(requireContext(),
                    "End of Treatment Form was submitted successfully.",
                    this@EndTreatmentReviewFragment,
                    R.id.action_endTreatmentReviewFragment_to_patientCardFragment
                )
            }else{
                //Save was not okay
                BlurBackgroundDialog(requireContext(),
                    "There was an issue with the referral request.",
                    this@EndTreatmentReviewFragment,
                    R.id.action_endTreatmentReviewFragment_to_patientCardFragment
                )
            }
            blurBackgroundDialog.show()



        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}