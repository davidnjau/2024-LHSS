package com.intellisoftkenya.a24cblhss.referrals.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModel

class ReferralDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ReferralDetailsFragment()
    }

    private val viewModel: ReferralDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_referral_details, container, false)
    }
}