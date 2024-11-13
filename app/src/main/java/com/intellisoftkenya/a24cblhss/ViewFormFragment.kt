package com.intellisoftkenya.a24cblhss

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.clinical_info.shared.ClinicalChildAdapter
import com.intellisoftkenya.a24cblhss.databinding.FragmentViewFormBinding
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.shared.FormDataAdapter
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.NotificationAdapter
import com.intellisoftkenya.a24cblhss.shared.NotificationServiceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewFormFragment : Fragment() {

    private var _binding: FragmentViewFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var fhirEngine: FhirEngine
    private lateinit var formatterClass: FormatterClass
    private val viewModel: NotificationServiceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentViewFormBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        fhirEngine = FhirApplication.fhirEngine(requireContext())

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationBasedOn = formatterClass
            .getSharedPref("","notificationBasedOn")
        if (notificationBasedOn != null){

            CoroutineScope(Dispatchers.IO).launch {

                val notificationData =
                    viewModel.getNotificationDataList(notificationBasedOn)

                CoroutineScope(Dispatchers.Main).launch {

                    val formDataAdapter = ClinicalChildAdapter(notificationData.formDataList)

                    binding.childRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.childRecyclerView.adapter = formDataAdapter
                    binding.tvTitle.text = notificationData.title
                }

                val communicationId = formatterClass.getSharedPref("","communicationId")
                if (communicationId != null) {
                    viewModel.updateCommunicationStatus(communicationId)
                }
            }
        }
    }
}