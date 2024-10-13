package com.intellisoftkenya.a24cblhss.clinical_info.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClinicalInfoViewViewModel : ViewModel() {
    // LiveData to hold the spinner selection
    private val _selectedItem = MutableLiveData<String>()
    val selectedItem: LiveData<String> get() = _selectedItem

    // Method to update the selected item in LiveData
    fun updateSelectedItem(item: String) {
        _selectedItem.value = item
    }
}