package com.intellisoftkenya.a24cblhss.dynamic_components

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.core.motion.utils.Utils
import androidx.core.content.ContextCompat
import com.intellisoftkenya.a24cblhss.R
import java.util.Calendar


class FieldManager(
    private val labelCustomizer: LabelCustomizer,
    private val context: Context) {

    fun addTextView(
        label: String,
        isMandatory: Boolean,
        parentLayout: LinearLayout) {
        val labelText = if (isMandatory) "$label *" else label
        val textView = TextView(context).apply {
            text = labelText
        }
        labelCustomizer.applyCustomization(textView)
        parentLayout.addView(textView)
    }

    fun addField(
        fieldCreator: FieldCreator,
        label: String,
        isMandatory: Boolean,
        parentLayout: LinearLayout,
        inputType: Int?,
        isEnabled: Boolean = true,
        isPastDate: Boolean = true
        ) {

        val field = fieldCreator.createField(
            label,
            isMandatory,
            inputType,
            isEnabled,
            isPastDate
        )
        parentLayout.addView(field)
    }

}

