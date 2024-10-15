package com.intellisoftkenya.a24cblhss.dynamic_components

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import com.google.gson.Gson
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbField
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.DbWidgets
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass

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
            val isEnabled = field.isEnabled
            val inputType = field.inputType
            val isPastDate = field.isPastDate

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
                        RadioButtonFieldCreator(
                            optionList,
                            context,
                            isHorizontal = true)
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
                    fieldManager.addField(
                        newField,
                        label,
                        isMandatory,
                        rootLayout,
                        inputType,
                        isEnabled,
                        isPastDate
                    )
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
                    is MandatoryRadioGroup -> {

                        val selectedText = childView.getSelectedRadioButtonText()
                        val isMandatory = childView.isMandatory
                        val tag = childView.tag?.toString() ?: ""

                        if (isMandatory){
                            if (tag.isNotEmpty() && selectedText != null){
                                val formData = DbFormData(tag, selectedText)
                                addedFields.add(formData)
                            }else{
                                // Add missing mandatory fields if text is empty
                                val tagText = if (tag=="DOB_SELECTION") "Date of Birth" else tag
                                missingFields.add(DbFormData(tagText, ""))
                            }
                        }else{
                            if (tag.isNotEmpty() && selectedText != null) {
                                val formData = DbFormData(tag, selectedText)
                                addedFields.add(formData)
                            }
                        }
                    }

                    is MandatoryEditText -> {
                        val tag = childView.tag?.toString() ?: ""
                        val text = childView.text.toString()
                        val isMandatory = childView.isMandatory

                        if (isMandatory){
                            if (tag.isNotEmpty() && text.isNotEmpty()){

                                val formData = DbFormData(tag, text)
                                addedFields.add(formData)
                            }else{
                                // Add missing mandatory fields if text is empty
                                missingFields.add(DbFormData(tag, ""))
                            }
                        }else{
                            if (tag.isNotEmpty() && text.isNotEmpty()) {
                                val formData = DbFormData(tag, text)
                                addedFields.add(formData)
                            }
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

                    is TextView -> {
                        val text = childView.text.toString()
                        val tag = childView.tag?.toString()?: ""

                        println("text1 $text")
                        println("tag1 $tag")

                        if (tag.isNotEmpty() && text.isNotEmpty()) {
                            val formData = DbFormData(tag, text)
                            addedFields.add(formData)
                        }
                    }

                    // Add more cases as needed based on widget types
                }
            }
        }

        return Pair(ArrayList(addedFields), ArrayList(missingFields))


    }

    fun populateFormData(
        formDataList: ArrayList<FormData>,
        parentLayout: LinearLayout) {

        formDataList.forEach { formData ->
            formData.formDataList.forEach { dbFormData ->
                // Find the widget by its tag in the parent layout
                val view = parentLayout.findViewWithTag<View>(dbFormData.tag)

                // Check for the type of view and set the appropriate text/value
                when (view) {
                    is EditText -> {
                        view.setText(dbFormData.text)
                    }
                    is Spinner -> {
                        // Assuming your spinner has an ArrayAdapter with strings, find the correct position
                        val adapter = view.adapter as ArrayAdapter<String>
                        val position = adapter.getPosition(dbFormData.text)
                        if (position >= 0) {
                            view.setSelection(position)
                        }
                    }
                    is RadioGroup -> {
                        // Assuming the tag refers to the RadioGroup, and dbFormData.text refers to the text of a RadioButton
                        for (i in 0 until view.childCount) {
                            val radioButton = view.getChildAt(i) as RadioButton
                            if (radioButton.text == dbFormData.text) {
                                radioButton.isChecked = true
                                break
                            }
                        }
                    }
                    // Add handling for other types of widgets here as needed
                    else -> {
                        // Optionally handle other widget types
                    }
                }
            }
        }
    }

    fun loadFormData(
        context: Context,
        rootLayout: LinearLayout,
        navigationDetails:String,
        fragmentClass:String
    ) {

        val formatterClass = FormatterClass(context)

        val gson = Gson()
        val formDataList = ArrayList<FormData>()

        val savedJson = formatterClass.getSharedPref(
            navigationDetails,
            fragmentClass
        )
        if (savedJson != null){
            val formDataFromJson = gson.fromJson(savedJson, FormData::class.java)
            if (formDataFromJson != null){
                formDataList.addAll(listOf(formDataFromJson))
                if (formDataList.isNotEmpty()){
                    populateFormData(formDataList, rootLayout)
                }
            }
        }

    }



}