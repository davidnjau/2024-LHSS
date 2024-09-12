package com.intellisoftkenya.a24cblhss.dynamic_components

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner

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


}