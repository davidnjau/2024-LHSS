package com.intellisoftkenya.a24cblhss.registration.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intellisoftkenya.a24cblhss.registration.viewmodel.DemographicsViewModel
import com.intellisoftkenya.a24cblhss.R

class DemographicsFragment : Fragment() {

    companion object {
        fun newInstance() = DemographicsFragment()
    }

    private val viewModel: DemographicsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_demographics, container, false)
    }
}