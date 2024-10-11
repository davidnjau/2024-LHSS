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
            "Clinical Info Section 1" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.CLINICAL_REFERRAL_I.name)
                findNavController().navigate(R.id.clinicalInfoFormI_IIFragment)
            }
            "Clinical Info Section 2" -> {
                formatterClass.saveSharedPref("", "CLINICAL_REFERRAL",
                    DbClasses.CLINICAL_REFERRAL_II.name)
                findNavController().navigate(R.id.clinicalInfoFormI_IIFragment)
            }
            "Clinical Info Section 3" -> {
                findNavController().navigate(R.id.clinicalInfoFormIII_IVFragment)
            }
            "Clinical Info Section 4" -> {
                findNavController().navigate(R.id.clinicalInfoFormIII_IVFragment)
            }
            "Clinical Info Section 5" -> {
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