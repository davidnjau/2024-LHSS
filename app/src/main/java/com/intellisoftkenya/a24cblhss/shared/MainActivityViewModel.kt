package com.intellisoftkenya.a24cblhss.shared

import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel(){
    // LiveData to hold the form data
    private val _formData = MutableLiveData<MutableMap<String, String>>()
    val formData: LiveData<MutableMap<String, String>> get() = _formData

    // Initialize the form data map
    private val formDataMap = mutableMapOf<String, String>()

    // LiveData to hold the selected radio button's value
    private val _selectedOption = MutableLiveData<String>()
    val selectedOption: LiveData<String> get() = _selectedOption

    // Method to update LiveData when a new option is selected
    fun updateSelectedOption(option: String) {
        _selectedOption.value = option
    }

    // Update form data when any field is filled or modified
    fun updateFormData(key: String, value: String) {
        formDataMap[key] = value
        _formData.value = formDataMap
    }

    // Optionally clear form data if needed
    fun clearFormData() {
        formDataMap.clear()
        _formData.value = formDataMap
    }

    // Function to extract form data, including radio button selections
    fun extractFormData(rootLayout: LinearLayout) {
        // Traverse through all child views of rootLayout
        for (i in 0 until rootLayout.childCount) {
            when (val childView = rootLayout.getChildAt(i)) {
                is RadioGroup -> {
                    // Set a listener to detect changes in RadioGroup selections
                    childView.setOnCheckedChangeListener { group, checkedId ->
                        val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                        val selectedText = selectedRadioButton.text.toString()

                        // Update LiveData with the new radio button selection
                        updateSelectedOption(selectedText)
                    }
                }

            }
        }
    }
}