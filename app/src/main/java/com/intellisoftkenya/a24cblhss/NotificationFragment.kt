package com.intellisoftkenya.a24cblhss

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.clinical_info.shared.ClinicalEncounterAdapter
import com.intellisoftkenya.a24cblhss.databinding.FragmentNotificationBinding
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModel
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModelFactory
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.NotificationAdapter
import com.intellisoftkenya.a24cblhss.shared.NotificationServiceViewModel
import com.intellisoftkenya.a24cblhss.shared.NotificationServiceViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var fhirEngine: FhirEngine
    private lateinit var formatterClass: FormatterClass
    private lateinit var viewModel: NotificationServiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        formatterClass = FormatterClass(requireContext())
        fhirEngine = FhirApplication.fhirEngine(requireContext())
        viewModel =
            ViewModelProvider(
                this,
                NotificationServiceViewModelFactory(
                    requireContext().applicationContext as Application,
                    fhirEngine,
                    "",
                ),
            )[NotificationServiceViewModel::class.java]

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {

            val notificationList = viewModel.getCommunicationList()

            Log.e("----->","<------")
            println("Notification List: $notificationList")
            Log.e("----->","<------")

            val formDataAdapter = NotificationAdapter(
                requireActivity().applicationContext,
                notificationList)

            CoroutineScope(Dispatchers.Main).launch {
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = formDataAdapter
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}