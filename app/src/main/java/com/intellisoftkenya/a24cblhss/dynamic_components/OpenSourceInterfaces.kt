package com.intellisoftkenya.a24cblhss.dynamic_components

import android.text.InputType
import android.view.View
import android.widget.TextView

// Interface for customizing TextView (label)
interface LabelCustomizer {
    fun applyCustomization(textView: TextView)
}

// Interface for creating input fields (SRP)
interface FieldCreator {
    fun createField(label: String,
                    isMandatory: Boolean = false,
                    inputType: Int = InputType.TYPE_CLASS_TEXT): View
}

