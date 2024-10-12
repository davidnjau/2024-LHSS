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
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoDetailsViewModel
import com.intellisoftkenya.a24cblhss.databinding.FragmentPatientFileBinding
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientCardViewModel
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class PatientFileFragment : Fragment() {

    private var _binding: FragmentPatientFileBinding? = null
    private val binding get() = _binding!!
    private lateinit var clinicalViewModel: ClinicalInfoDetailsViewModel
    private var patientId:String = ""
    private lateinit var formatterClass: FormatterClass

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

            clinicalViewModel.createCarePlan()

            findNavController().navigate(
                R.id.action_patientFileFragment_to_clinicalInfoSectionsFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun showReceivePatientDialog() {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Warning")
        // Set dialog message
        builder.setMessage("Ending Treatment will close the patient's file. " +
                "Only one file can be active at once.\n\n Do you want to End Treatment?")

        // Set Yes button and its action
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Trigger the form when Yes is clicked
            dialog.dismiss() // Close the dialog
            findNavController().navigate(
                R.id.action_patientFileFragment_to_endTreatmentFormFragment)
        }

        // Set No button and its action
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Just close the dialog when No is clicked
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

}