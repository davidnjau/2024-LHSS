package com.intellisoftkenya.a24cblhss.dynamic_components

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
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

    fun populateView1(
        dbFieldList:ArrayList<DbField>,
        rootLayout: LinearLayout,
        fieldManager: FieldManager,
        context: Context
        ){

        dbFieldList.forEach {
            val widget = it.widgets
            val optionList = it.optionList
            val label = it.label
            val isMandatory = it.isMandatory

            // This is the label of the widget
            fieldManager.addTextView(
                label,
                isMandatory,
                rootLayout
            )

            val fieldWidgets = when (widget) {
                DbWidgets.EDIT_TEXT.name -> {
                    EditTextFieldCreator(context)
                }
                DbWidgets.SPINNER.name -> {
                    SpinnerFieldCreator(
                        optionList ,
                        context
                    )
                }
                else -> {
                    null
                }
            }
            if (fieldWidgets != null){
                fieldManager.addField(
                    fieldWidgets,
                    label,
                    isMandatory,
                    rootLayout
                )
            }
        }
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


}