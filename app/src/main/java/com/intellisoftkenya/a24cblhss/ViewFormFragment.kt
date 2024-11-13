package com.intellisoftkenya.a24cblhss

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.databinding.FragmentViewFormBinding
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

class ViewFormFragment : Fragment() {

    private var _binding: FragmentViewFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var fhirEngine: FhirEngine
    private lateinit var formatterClass: FormatterClass

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

        val notificationBasedOn = formatterClass.getSharedPref("","notificationBasedOn")
        if (notificationBasedOn != null){

            Log.e("------->","<-------")
            println("Notification based on: $notificationBasedOn")
            Log.e("------->","<-------")

        }

    }

}