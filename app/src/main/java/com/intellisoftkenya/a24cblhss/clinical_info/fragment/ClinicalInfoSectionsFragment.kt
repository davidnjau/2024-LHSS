package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalLayoutListViewModel
import com.intellisoftkenya.a24cblhss.clinical_info.viewmodel.ClinicalLayoutsRecyclerViewAdapter
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.LayoutsRecyclerViewAdapter

class ClinicalInfoSectionsFragment : Fragment() {

    private val layoutViewModel: ClinicalLayoutListViewModel by viewModels()

    private lateinit var formatterClass: FormatterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        formatterClass = FormatterClass(requireContext())
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clinical_info_sections, container, false)
    }

    private fun onItemClick(layout: ClinicalLayoutListViewModel.Layout) {
        when (layout.textId) {
            "Tb Treatment" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.TB_TREATMENT.name)
                findNavController().navigate(R.id.clinicalInfoFormI_IIFragment)
            }
            "HIV Status and Treatment" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.HIV_STATUS_TREATMENT.name)
                findNavController().navigate(R.id.clinicalInfoFormI_IIFragment)
            }
            "Laboratory Results" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.LABORATORY_RESULTS.name)
                findNavController().navigate(R.id.clinicalInfoFormIII_IVFragment)
            }
            "DST" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.DST.name)
                findNavController().navigate(R.id.clinicalInfoFormIII_IVFragment)
            }
            "DR TB Follow Up Test" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.DR_TB_FOLLOW_UP_TEST.name)
                findNavController().navigate(R.id.clinicalInfoFormIII_IVFragment)
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutList = layoutViewModel.getLayoutList()

        val adapter = ClinicalLayoutsRecyclerViewAdapter(::onItemClick).apply { submitList(layoutList) }
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.sdcLayoutsRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }
}