package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentClinicalInfoFormIIIIVBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ClinicalInfoFormIII_IVFragment : Fragment() {

    private var _binding: FragmentClinicalInfoFormIIIIVBinding? = null

    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass
    private var patientId:String = ""
    private var serviceRequestId:String = ""
    private var workflowTitles:String = ""

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

        binding.btnAdd.setOnClickListener {
            val bottomNavigationDrawerFragment =
                BottomNavigationDrawerFragmentWithWidgets()
            bottomNavigationDrawerFragment.show(parentFragmentManager,
                bottomNavigationDrawerFragment.tag)
        }

        return binding.root

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