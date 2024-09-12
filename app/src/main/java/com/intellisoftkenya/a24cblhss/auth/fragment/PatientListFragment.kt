package com.intellisoftkenya.a24cblhss.auth.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.intellisoftkenya.a24cblhss.auth.viewmodel.PatientListViewModel
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentPatientListBinding
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager

class PatientListFragment : Fragment() {
    private var _binding: FragmentPatientListBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldManager: FieldManager


    private val viewModel: PatientListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPatientListBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPatient.setOnClickListener {
            findNavController().navigate(R.id.action_patientListFragment_to_patientCardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}