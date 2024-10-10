package com.intellisoftkenya.a24cblhss.clinical_info.viewmodel

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.intellisoftkenya.a24cblhss.R

class ClinicalLayoutListViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    fun getLayoutList(): List<Layout> {
        return Layout.values().toList()
    }

    enum class Layout(
        @DrawableRes val iconId: Int,
        val textId: String,
    ) {
        CLINICAL_INFO_SECTION_I(R.drawable.ic_action_next_kin, "Clinical Info Section 1"),
        CLINICAL_INFO_SECTION_II(R.drawable.ic_action_next_kin, "Clinical Info Section 2"),
        CLINICAL_INFO_SECTION_III(R.drawable.ic_action_next_kin, "Clinical Info Section 3"),
        CLINICAL_INFO_SECTION_IV(R.drawable.ic_action_next_kin, "Clinical Info Section 4"),
        CLINICAL_INFO_SECTION_V(R.drawable.ic_action_next_kin, "Clinical Info Section 5"),
    }
}