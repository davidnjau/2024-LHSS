package com.intellisoftkenya.a24cblhss.referrals.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.databinding.FragmentAcknoledgementDetailsBinding
import com.intellisoftkenya.a24cblhss.databinding.FragmentReferralPatientListBinding
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.AcknoledgementDetailsViewModel
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralPatientListViewModel
import com.intellisoftkenya.a24cblhss.shared.DbPatientItem
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.PatientAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class ReferralPatientList : Fragment() {
    private var _binding: FragmentReferralPatientListBinding? = null
    private val binding get() = _binding!!

    private lateinit var fhirEngine: FhirEngine
    private lateinit var formatterClass: FormatterClass

    private lateinit var viewModel: ReferralPatientListViewModel

    private var patientList =  ArrayList<DbPatientItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReferralPatientListBinding.inflate(inflater, container, false)

        formatterClass = FormatterClass(requireContext())

        fhirEngine = FhirApplication.fhirEngine(requireContext())

        viewModel =
            ViewModelProvider(
                this,
                ReferralPatientListViewModel.ReferralPatientListViewModelFactory(
                    requireActivity().application,
                ),
            )[ReferralPatientListViewModel::class.java]

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle search functionality
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchPatientsByName(it) }

                if (newText != null) {
                    patientList.clear()
                    patientList = searchPatientsByName(patientList, newText)
                    populateRecyclerView(patientList)
                }
                return false
            }
        })



        // Handle DatePicker icon click
        binding.datepickerIcon.setOnClickListener {
            binding.datePickerLayout.visibility = View.VISIBLE
        }

        // Handle close DatePicker layout
        binding.closeDatePicker.setOnClickListener {
            binding.datePickerLayout.visibility = View.GONE
        }

        viewModel.liveSearchedPatients.observe(viewLifecycleOwner) {

            patientList = ArrayList(it)
            // Initialize RecyclerView and adapter
            populateRecyclerView(patientList)
        }

    }

    fun populateRecyclerView(patientList: List<DbPatientItem>) {

        val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)

        val distinctPatientList = patientList.distinctBy { it.name }

        val sortedPatientList = distinctPatientList.sortedWith(compareByDescending<DbPatientItem> {
            try {
                // Parse date if it's not empty
                if (it.dateCreated.isNullOrEmpty()) null else dateFormat.parse(it.dateCreated)
            } catch (e: Exception) {
                null // Handle unparseable date
            }
        }.thenBy {
            it.dateCreated.isNullOrEmpty() // Push empty dateCreated to the bottom
        })

        val patientAdapter = PatientAdapter(sortedPatientList) { selectedPatient ->

            val id = selectedPatient.id
            formatterClass.saveSharedPref("","patientId", id)
            findNavController().navigate(R.id.action_referralPatientList_to_referralListFragment)

        }

        binding.patientRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.patientRecyclerView.adapter = patientAdapter

        // Set total patients
        binding.totalPatientsTextView.text = "Total Patients: ${distinctPatientList.size}"
    }
    fun searchPatientsByName(patients: List<DbPatientItem>, query: String): ArrayList<DbPatientItem> {
        return ArrayList(patients.filter { it.name.contains(query, ignoreCase = true) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}