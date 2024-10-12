package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.shared.PatientFileAdapter
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoDetailsViewModel
import com.intellisoftkenya.a24cblhss.databinding.FragmentPatientFileBinding
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientCardViewModel
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.shared.DbCarePlan
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientFileFragment : Fragment() {

    private var _binding: FragmentPatientFileBinding? = null
    private val binding get() = _binding!!
    private lateinit var clinicalViewModel: ClinicalInfoDetailsViewModel
    private var patientId:String = ""
    private lateinit var formatterClass: FormatterClass

    private var carePlanList = ArrayList<DbCarePlan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPatientFileBinding.inflate(inflater, container, false)

//        binding.btnEndTreatment.setOnClickListener {
//            showReceivePatientDialog()
//        }

        formatterClass = FormatterClass(requireContext())
        patientId = formatterClass.getSharedPref("", "patientId") ?: ""

        clinicalViewModel =
            ViewModelProvider(
                this,
                ClinicalInfoDetailsViewModel.ClinicalInfoDetailsViewModelFactory(
                    requireActivity().application,
                    patientId
                )
            )[ClinicalInfoDetailsViewModel::class.java]

        binding.btnAddNewPatientFile.setOnClickListener {

            val hasActiveStatus = carePlanList.any { it.status == "ACTIVE" }
            if (hasActiveStatus) {
                formatterClass.showDialog("Active File Found",
                    "You cannot create a new file while there is an active file.")
            }else {
                clinicalViewModel.createCarePlan()

                findNavController().navigate(
                    R.id.action_patientFileFragment_to_clinicalInfoSectionsFragment)
            }

        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            carePlanList = clinicalViewModel.getCarePlanDetails()

            val formDataAdapter = PatientFileAdapter(
                requireContext(),
                this@PatientFileFragment,
                carePlanList)
            CoroutineScope(Dispatchers.Main).launch {
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = formDataAdapter
            }

        }

    }
}