package com.intellisoftkenya.a24cblhss.dynamic_components

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.DbWidgets

object FormUtils {
    fun extractFormData(
        rootLayout: LinearLayout
    ): ArrayList<DbFormData> {

        val formDataList = ArrayList<DbFormData>()

        // Traverse through all child views of rootLayout
        for (i in 0 until rootLayout.childCount) {

            when (val childView = rootLayout.getChildAt(i)) {
                is EditText -> {
                    val tag = childView.tag.toString()
                    val text = childView.text.toString()
                    formDataList.add(DbFormData(tag, text))
                }
                is Spinner -> {
                    // Extract selected identification type from spinner
                    val text = childView.selectedItem.toString()
                    val tag = childView.tag.toString()
                    formDataList.add(DbFormData(tag, text))
                }
            }
        }

        return formDataList
    }

    fun populateView(
        dbFieldList: ArrayList<DbField>,
        rootLayout: LinearLayout,
        fieldManager: FieldManager,
        context: Context
    ) {

        dbFieldList.forEach { field ->
            val widgetType = field.widgets
            val optionList = field.optionList
            val label = field.label
            val isMandatory = field.isMandatory

            // Look for an existing view with the same label as the tag in the root layout
            var existingView: View? = null
            for (i in 0 until rootLayout.childCount) {
                val child = rootLayout.getChildAt(i)
                if (child.tag == label) {
                    existingView = child
                    break
                }
            }

            // Update the existing view or create a new one
            if (existingView != null) {
                // Update the existing widget
                when (existingView) {
                    is EditText -> {
                        EditTextFieldCreator(context)
                    }
                    is Spinner -> {
                        if (widgetType == DbWidgets.SPINNER.name) {
                            SpinnerFieldCreator(
                                optionList ,
                                context
                            )
                        }
                    }
                    is RadioButton -> {
                        RadioButtonFieldCreator(optionList, context, isHorizontal = true)
                    }
                    is DatePicker -> {
                        DatePickerFieldCreator(
                            context
                        )
                    }
                }
            } else {
                // Create a new widget if it doesn't exist
                // Add the label as a TextView
                fieldManager.addTextView(label, isMandatory, rootLayout)

                val newField = when (widgetType) {
                    DbWidgets.EDIT_TEXT.name -> {
                        EditTextFieldCreator(context)
                    }
                    DbWidgets.SPINNER.name -> {
                        SpinnerFieldCreator(
                            optionList ,
                            context
                        )
                    }
                    DbWidgets.RADIO_BUTTON.name -> {
                        RadioButtonFieldCreator(
                            optionList ,
                            context,
                            isHorizontal = true
                        )
                    }
                    DbWidgets.DATE_PICKER.name -> {
                        DatePickerFieldCreator(
                            context
                        )
                    }

                    else -> {
                        null
                    }
                }

                // Add the new widget to the layout
                if (newField != null) {
                    fieldManager.addField(newField, label, isMandatory, rootLayout)
                }
            }
        }
    }

    fun removeWidgetByTag(parentLayout: LinearLayout, tag: String) {
        // Loop through the children of the parent layout
        for (i in 0 until parentLayout.childCount) {
            val childView = parentLayout.getChildAt(i)
            // Check if the child's tag matches the tag to be removed
            if (childView.tag == tag) {
                // Remove the view from the parent layout
                parentLayout.removeView(childView)
                break // Exit the loop once the view is removed
            }
        }
    }

    fun extractAllFormData(rootLayout: LinearLayout):
            Pair<ArrayList<DbFormData>, ArrayList<DbFormData>> {

        val missingFields = ArrayList<DbFormData>()
        val addedFields = ArrayList<DbFormData>()

        // Traverse through all child views of rootLayout
        for (i in 0 until rootLayout.childCount) {
            val childView = rootLayout.getChildAt(i)

            if (childView.visibility == View.VISIBLE) { // Only process visible views
                when (childView) {
                    is RadioGroup -> {
                        // Get the selected RadioButton ID
                        val selectedRadioButtonId = childView.checkedRadioButtonId

                        if (selectedRadioButtonId != -1) {
                            // Find the selected RadioButton using the selected ID
                            val selectedRadioButton = childView.findViewById<RadioButton>(selectedRadioButtonId)
                            val selectedText = selectedRadioButton.text.toString()

                            val tag = childView.tag?.toString() ?: ""
                            if (tag.isNotEmpty() && selectedText.isNotEmpty()) {
                                val formData = DbFormData(tag, selectedText)
                                addedFields.add(formData)
                            } else if (tag.isNotEmpty()) {
                                // Add missing mandatory fields if text is empty
                                missingFields.add(DbFormData(tag, ""))
                            }
                        }
                    }

                    is EditText -> {
                        val tag = childView.tag?.toString() ?: ""
                        val text = childView.text.toString()

                        if (tag.isNotEmpty() && text.isNotEmpty()) {
                            val formData = DbFormData(tag, text)
                            addedFields.add(formData)
                        } else if (tag.isNotEmpty()) {
                            // Add missing mandatory fields if text is empty
                            missingFields.add(DbFormData(tag, ""))
                        }
                    }

                    is Spinner -> {
                        val selectedText = childView.selectedItem.toString()
                        val tag = childView.tag?.toString() ?: ""

                        if (tag.isNotEmpty() && selectedText.isNotEmpty()) {
                            val formData = DbFormData(tag, selectedText)
                            addedFields.add(formData)
                        } else if (tag.isNotEmpty()) {
                            // Add missing mandatory fields if text is empty
                            missingFields.add(DbFormData(tag, ""))
                        }
                    }

                    is CheckBox -> {
                        val tag = childView.tag?.toString() ?: ""
                        val isChecked = childView.isChecked

                        if (tag.isNotEmpty()) {
                            val formData = DbFormData(tag, if (isChecked) "Checked" else "Unchecked")
                            addedFields.add(formData)
                        }
                    }

                    // Add more cases as needed based on widget types
                }
            }
        }

        return Pair(ArrayList(addedFields), ArrayList(missingFields))


    }



}