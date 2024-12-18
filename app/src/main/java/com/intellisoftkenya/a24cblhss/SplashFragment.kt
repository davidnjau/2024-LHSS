package com.intellisoftkenya.a24cblhss

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.databinding.FragmentSplashBinding
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralPatientListViewModel
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import com.intellisoftkenya.a24cblhss.shared.NotificationServiceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Resource

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSplashBinding
    private lateinit var formatterClass : FormatterClass
    private lateinit var viewModel: ReferralPatientListViewModel
    private val notificationServiceViewModel: NotificationServiceViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        formatterClass = FormatterClass(requireContext())
        formatterClass.clearData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val appName = requireContext().getString(R.string.app_name)
        binding = FragmentSplashBinding.inflate(layoutInflater)
        Handler().postDelayed({
            if (formatterClass.getSharedPref(appName,"isLoggedIn") == "true") {
                findNavController().navigate(R.id.landingPageFragment)
            } else {
                findNavController().navigate(R.id.loginFragment)
            }

//            findNavController().navigate(R.id.addressFragment)

        }, 1000)

        viewModel =
            ViewModelProvider(
                this,
                ReferralPatientListViewModel.ReferralPatientListViewModelFactory(
                    requireActivity().application,
                ),
            )[ReferralPatientListViewModel::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.referralNumber()
            notificationServiceViewModel.getCommunicationList()
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SplashFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}