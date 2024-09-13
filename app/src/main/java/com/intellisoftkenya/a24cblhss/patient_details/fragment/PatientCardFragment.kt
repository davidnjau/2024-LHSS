package com.intellisoftkenya.a24cblhss.patient_details.fragment

import android.app.Application
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
import com.intellisoftkenya.a24cblhss.databinding.FragmentPatientCardBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientCardViewModel
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class PatientCardFragment : Fragment() {

    private var _binding: FragmentPatientCardBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager
    private lateinit var formatterClass: FormatterClass

    private lateinit var patientDetailsViewModel: PatientCardViewModel
    private lateinit var fhirEngine: FhirEngine
    private var patientId:String = ""
    private lateinit var formDataAdapter: FormDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPatientCardBinding.inflate(inflater, container, false)

        formatterClass = FormatterClass(requireContext())

        patientId = formatterClass.getSharedPref("", "patientId") ?: ""

        fhirEngine = FhirApplication.fhirEngine(requireContext())

        patientDetailsViewModel =
            ViewModelProvider(
                this,
                PatientDetailsViewModelFactory(
                    requireContext().applicationContext as Application,
                    fhirEngine,
                    patientId
                ),
            )
                .get(PatientCardViewModel::class.java)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReferrals.setOnClickListener {
            findNavController().navigate(R.id.action_patientCardFragment_to_referralListFragment)
        }
        binding.btnReferPatient.setOnClickListener {
            findNavController().navigate(R.id.action_patientCardFragment_to_referPatientFragment)
        }

        val formDataList = patientDetailsViewModel.getPatientInfo()

        formDataAdapter = FormDataAdapter(formDataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.recyclerView.adapter = formDataAdapter

        val fullName = formatterClass.getNameFields(formDataList)
        binding.tvFullName.text = fullName

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}