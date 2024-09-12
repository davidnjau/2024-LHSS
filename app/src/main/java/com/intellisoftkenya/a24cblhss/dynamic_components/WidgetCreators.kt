package com.intellisoftkenya.a24cblhss.dynamic_components

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.intellisoftkenya.a24cblhss.R
import java.util.Calendar

/**
 * This contains all the dynamic Widgets
 */

// Concrete implementation for label customization (OCP)
class DefaultLabelCustomizer : LabelCustomizer {
    override fun applyCustomization(
        textView: TextView
    ) {
        // Ensure that the TextView has layoutParams
        val layoutParams = textView.layoutParams ?: ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        ) .apply {
            setMargins(8,8,8,0)
        }

        textView.layoutParams = layoutParams
        textView.textSize = 18f

        // Set text color and padding
        textView.setTextColor(Color.BLACK)
        textView.setPadding(16, 16, 16, 16)
    }
}

// Concrete implementation for creating an EditText field
class EditTextFieldCreator(
    private val context: Context
) : FieldCreator {
    override fun createField(
        label: String,
        isMandatory: Boolean,
        inputType: Int
    ): View {
        val editText = EditText(context).apply {
            this.hint = label
            this.inputType = inputType
            this.background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext) // Set rounded border
            this.tag = label

            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
        }
        return editText
    }
}

// Concrete implementation for creating a Spinner field (LSP)
class SpinnerFieldCreator(
    private val options: List<String>,
    private val context: Context) : FieldCreator {
    override fun createField(
        label: String,
        isMandatory: Boolean,
        inputType: Int): View {
        val spinner = Spinner(context)
        spinner.tag = label
        val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        return spinner
    }
}

// Concrete implementation for creating a Radio button field (LSP)
class RadioButtonFieldCreator(
    private val optionList: List<String>,
    private val context: Context,
    private val isHorizontal: Boolean = false // Default is vertical
) : FieldCreator {
    override fun createField(
        label: String,
        isMandatory: Boolean,
        inputType: Int
    ): View {
        // Create a RadioGroup
        val radioGroup = RadioGroup(context).apply {
            orientation = if (isHorizontal) RadioGroup.HORIZONTAL else RadioGroup.VERTICAL
            tag = label
        }

        // Dynamically add RadioButtons to the RadioGroup
        optionList.forEach { option ->
            val radioButton = RadioButton(context).apply {
                text = option
                tag = label
            }
            radioGroup.addView(radioButton)
        }

        // Return the RadioGroup as the view
        return radioGroup
    }
}

//Concrete implementation for creating a Datepicker
class DatePickerFieldCreator(private val context: Context) : FieldCreator {

    override fun createField(
        label: String,
        isMandatory: Boolean,
        inputType: Int
    ): View {

        // Create an EditText field to display the selected date
        val dateEditText = EditText(context).apply {
            isFocusable = false // Prevent typing
            background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext)
            hint = if (isMandatory) "$label *" else label

            // Set drawable to the right (use 0 for other positions if no drawable is needed)
            val rightIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_date) // Your drawable resource
            setCompoundDrawablesWithIntrinsicBounds(null, null, rightIcon, null)

            setOnClickListener {
                // Show DatePickerDialog on click
                showDatePickerDialog(this)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
        }

        return dateEditText
    }

    // Function to show DatePickerDialog
    private fun showDatePickerDialog(editText: EditText) {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create DatePickerDialog
        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            // Format and set the selected date to EditText
            val selectedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                    "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
            editText.setText(selectedDate)
        }, year, month, day)


        // Show the dialog
        datePickerDialog.show()
    }
}
