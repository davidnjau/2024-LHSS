package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalInfoDetailsViewModel
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalLayoutListViewModel
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalLayoutsRecyclerViewAdapter
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoSectionsBinding
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.LayoutsRecyclerViewAdapter

class ClinicalInfoSectionsFragment : Fragment() {

    private val layoutViewModel: ClinicalLayoutListViewModel by viewModels()

    private lateinit var formatterClass: FormatterClass

    private val _binding: FragmentClinicalInfoSectionsBinding? = null
    private val binding get() = _binding!!
    private var patientId:String = ""
    private var carePlanId:String = ""
    private lateinit var clinicalViewModel: ClinicalInfoDetailsViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        formatterClass = FormatterClass(requireContext())
        patientId = formatterClass.getSharedPref("", "patientId") ?: ""
        carePlanId = formatterClass.getSharedPref(DbNavigationDetails.CARE_PLAN.name,"carePlanId") ?: ""

        clinicalViewModel =
            ViewModelProvider(
                this,
                ClinicalInfoDetailsViewModel.ClinicalInfoDetailsViewModelFactory(
                    requireActivity().application,
                    patientId
                )
            )[ClinicalInfoDetailsViewModel::class.java]

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clinical_info_sections, container, false)
    }

    private fun onItemClick(layout: ClinicalLayoutListViewModel.Layout) {

        var clinicalReferral = ""
        when (layout.textId) {
            "Tb Treatment" -> { clinicalReferral = DbClasses.TB_TREATMENT.name }
            "HIV Status and Treatment" -> { clinicalReferral = DbClasses.HIV_STATUS_TREATMENT.name }
            "Laboratory Results" -> { clinicalReferral = DbClasses.LABORATORY_RESULTS.name }
            "DST" -> { clinicalReferral = DbClasses.DST.name }
            "DR TB Follow Up Test" -> { clinicalReferral = DbClasses.DR_TB_FOLLOW_UP_TEST.name }

        }
        formatterClass.saveSharedPref("", "CLINICAL_REFERRAL", clinicalReferral)
        findNavController().navigate(R.id.action_clinicalInfoSectionsFragment_to_clinicalInfoEncountersFragment)


    }

    private fun showEndPatientDialog() {
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
                R.id.action_clinicalInfoSectionsFragment_to_endTreatmentFormFragment)
        }

        // Set No button and its action
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Just close the dialog when No is clicked
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formatterClass.deleteSharedPref("", "CLINICAL_REFERRAL")

        val btnEndTreatment = view.findViewById<Button>(R.id.btnEndTreatment)


//        val hasActiveStatus = carePlanList.any { it.status == "ACTIVE" }
//        if (hasActiveStatus) {
//            btnEndTreatment.visibility = View.VISIBLE
//        }else{
//            btnEndTreatment.visibility = View.GONE
//        }


        val layoutList = layoutViewModel.getLayoutList()

        btnEndTreatment.setOnClickListener { showEndPatientDialog() }

        val adapter = ClinicalLayoutsRecyclerViewAdapter(::onItemClick).apply { submitList(layoutList) }
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.sdcLayoutsRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }
}