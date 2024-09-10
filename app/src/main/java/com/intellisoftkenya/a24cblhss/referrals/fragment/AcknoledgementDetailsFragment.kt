package com.intellisoftkenya.a24cblhss.referrals.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.AcknoledgementDetailsViewModel
import com.intellisoftkenya.a24cblhss.R

class AcknoledgementDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = AcknoledgementDetailsFragment()
    }

    private val viewModel: AcknoledgementDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_acknoledgement_details, container, false)
    }
}