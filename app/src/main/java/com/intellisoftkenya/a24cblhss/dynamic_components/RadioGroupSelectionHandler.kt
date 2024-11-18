package com.intellisoftkenya.a24cblhss.dynamic_components

import android.widget.RadioButton
import android.widget.RadioGroup

interface RadioGroupSelectionHandler {
    fun handleSelection(radioGroup: MandatoryRadioGroup, updateSelectedItem: (String) -> Unit)
}

class DefaultRadioGroupSelectionHandler : RadioGroupSelectionHandler {
    override fun handleSelection(radioGroup: MandatoryRadioGroup, updateSelectedItem: (String) -> Unit) {
        // Set a listener on the RadioGroup to handle selection changes

        val selectedText = radioGroup.getSelectedRadioButtonText()
        if (selectedText != null) {
            updateSelectedItem(selectedText)
        }

    }
}

