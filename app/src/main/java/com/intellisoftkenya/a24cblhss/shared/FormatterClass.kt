package com.intellisoftkenya.a24cblhss.shared

import android.app.DatePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.compose.ui.semantics.Role.Companion.RadioButton
import androidx.core.content.ContextCompat
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.dynamic_components.MandatoryEditText
import com.intellisoftkenya.a24cblhss.dynamic_components.MandatoryRadioGroup

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class FormatterClass(private val context: Context) {

    private val dateInverseFormatSeconds: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }
    fun formatCurrentDateTime(date: Date): String {
        return dateInverseFormatSeconds.format(date)
    }
    fun addRadioButtonWithDatePicker(context: Context, linearLayout: LinearLayout) {
        // Create a RadioGroup for Accurate and Estimate options
        val radioGroup = MandatoryRadioGroup(context).apply {
            orientation = RadioGroup.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isMandatory = true
            tag = "DOB_SELECTION"
        }

        val acc = 1
        val est = 2

        // Ensure the RadioButtons have unique IDs
        val radioButtonAccurate = RadioButton(context).apply {
            text = "Accurate"
            tag= "Accurate"
            id = acc // Use a predefined ID or ensure it's unique
        }

        val radioButtonEstimate = RadioButton(context).apply {
            text = "Estimate"
            tag = "Estimate"
            id = est // Use a predefined ID or ensure it's unique
        }

        // Add both RadioButtons to the RadioGroup
        radioGroup.addRadioButton(radioButtonAccurate)
        radioGroup.addRadioButton(radioButtonEstimate)

        val textViewDateOfBirthLabel = TextView(context).apply {
            text = "Date of Birth *"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
            textSize = 18f
        }

        linearLayout.addView(textViewDateOfBirthLabel)

        // Add the RadioGroup to the LinearLayout
        linearLayout.addView(radioGroup)

        // TextView to show DatePickerDialog when Accurate is selected
        val editTextSelectedDate = MandatoryEditText(context).apply {
            background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext)
            isEnabled = false
            tag = "DOB"
            isMandatory = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
        }

        // TextView to show DatePickerDialog when Accurate is selected
        val textViewDate = TextView(context).apply {
            text = "Select Date"
            visibility = View.GONE  // Initially hidden, shown when Accurate is selected
            background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext)
            // Set drawable to the right (use 0 for other positions if no drawable is needed)
            val rightIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_date) // Your drawable resource
            setCompoundDrawablesWithIntrinsicBounds(null, null, rightIcon, null)

            setOnClickListener {
                showDatePickerDialog(context, editTextSelectedDate)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
        }

        // Add the TextView to the LinearLayout

        linearLayout.addView(editTextSelectedDate)

        linearLayout.addView(textViewDate)

        // Create the two EditTexts for Estimate (Year and Month inputs)
        val editTextYears = EditText(context).apply {
            hint = "Year"
            background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            visibility = View.GONE  // Hidden initially, shown when Estimate is selected
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
        }

        val editTextMonths = EditText(context).apply {
            hint = "Month"
            background = ContextCompat.getDrawable(context, R.drawable.rounded_edittext)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            visibility = View.GONE  // Hidden initially, shown when Estimate is selected
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8,5,8,12)
            }
            this.setPadding(32,16,20,16)
        }

        // Add the EditTexts to the LinearLayout (hidden initially)
        linearLayout.addView(editTextYears)
        linearLayout.addView(editTextMonths)

        // Add manual click listener for RadioButtons for debugging
        radioButtonAccurate.setOnClickListener {
            textViewDate.visibility = View.VISIBLE

            editTextYears.visibility = View.GONE
            editTextMonths.visibility = View.GONE
        }

        radioButtonEstimate.setOnClickListener {
            val dateFormat = "dd/mm/yyyy"
            editTextSelectedDate.setText(dateFormat)

            textViewDate.visibility = View.GONE

            editTextYears.visibility = View.VISIBLE
            editTextMonths.visibility = View.VISIBLE
        }

        // Add a TextWatcher to editTextYears
        editTextYears.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // You can log or take action before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Handle text changes here (e.g., validate input)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null){
                    val selectedYear = s.toString()
                    val selectedMonth = editTextMonths.text
                    val month = if (!TextUtils.isEmpty(selectedMonth)) selectedMonth else ""
                    val selectedDate = "$selectedYear-${month}-01"
                    editTextSelectedDate.setText(selectedDate)
                }

                // Action after the text is changed (e.g., process the input)
            }
        })

        // Add a TextWatcher to editTextMonths
        editTextMonths.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // You can log or take action before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null){
                    val selectedMonth = s.toString()
                    val years = editTextYears.text
                    val selectedYear = if (!TextUtils.isEmpty(years)) years else ""
                    val selectedDate = "$selectedYear-$selectedMonth-01"
                    editTextSelectedDate.setText(selectedDate)
                }
            }
        })

    }

    private fun showDatePickerDialog(context: Context, editTextSelectedDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                editTextSelectedDate.setText(selectedDate)

            },
            year, month, day
        )
        datePickerDialog.show()
    }



    fun clearData() {

        listOf(
            DbNavigationDetails.PATIENT_REGISTRATION.name,
            DbNavigationDetails.REFER_PATIENT.name,
            DbNavigationDetails.REFERRALS.name).forEach {
            clearSharedPreferences(it)
        }
        listOf("serviceRequestId", "patientId").forEach {
            deleteSharedPref("", it)
        }

    }

    fun saveSharedPref(
        sharedPrefName:String,
        key: String,
        value: String)
    {
        val sharedPreferenceName = if (sharedPrefName == ""){
            context.getString(R.string.app_name)
        }else{
            sharedPrefName
        }

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value);
        editor.apply();
    }

    fun getSharedPref(
        sharedPrefName:String,
        key: String): String? {
        val sharedPreferenceName = if (sharedPrefName == ""){
            context.getString(R.string.app_name)
        }else{
            sharedPrefName
        }
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE)
        return sharedPreferences.getString(key, null)

    }

    fun toSentenceCase(input:String): String {
        return input.lowercase()
            .replace("_"," ")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
    }

    // Function to clear all SharedPreferences data
    fun clearSharedPreferences(sharedPrefName:String,) {
        val sharedPreferenceName = if (sharedPrefName == ""){
            context.getString(R.string.app_name)
        }else{
            sharedPrefName
        }
        val sharedPreferences = context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Clear all the stored values
        editor.apply()  // Apply changes
    }

    fun deleteSharedPref(
        sharedPrefName:String,
        key: String) {
        val sharedPreferenceName = if (sharedPrefName == ""){
            context.getString(R.string.app_name)
        }else{
            sharedPrefName
        }
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(key);
        editor.apply();

    }


    fun convertDateFormat(inputDate: String): String? {
        // Define the input date formats to check
        val inputDateFormats = arrayOf(
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "yyyyMMdd",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "MM-dd-yyyy",
            "dd/MM/yyyy",
            "yyyyMMddHHmmss",
            "yyyy-MM-dd HH:mm:ss",
            "EEE, dd MMM yyyy HH:mm:ss Z",  // Example: "Mon, 25 Dec 2023 12:30:45 +0000"
            "yyyy-MM-dd'T'HH:mm:ssXXX",     // ISO 8601 with time zone offset (e.g., "2023-11-29T15:44:00+03:00")
            "EEE MMM dd HH:mm:ss zzz yyyy", // Example: "Sun Jan 01 00:00:00 GMT+03:00 2023"

            // Add more formats as needed
        )

        // Try parsing the input date with each format
        for (format in inputDateFormats) {
            try {
                val dateFormat = SimpleDateFormat(format, Locale.getDefault())
                dateFormat.isLenient = false // Set lenient to false
                val parsedDate = dateFormat.parse(inputDate)

                // If parsing succeeds, format and return the date in the desired format
                parsedDate?.let {
                    return SimpleDateFormat("MMM d yyyy", Locale.getDefault()).format(it)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                // Continue to the next format if parsing fails
            }
        }

        // If none of the formats match, return an error message or handle it as needed
        return null
    }

    fun getNameFields(formDataList: ArrayList<FormData>): String {
        var firstName: String = ""
        var middleName: String = ""
        var lastName: String = ""

        // Loop through formDataList
        formDataList.forEach { formData ->
            if (formData.title == "DEMOGRAPHICS") {
                // Loop through the list of DbFormData
                formData.formDataList.forEach { dbFormData ->
                    when (dbFormData.tag) {
                        "First Name" -> firstName = dbFormData.text
                        "Middle Name" -> middleName = dbFormData.text
                        "Last Name" -> lastName = dbFormData.text
                    }
                }
            }
        }

        // Return a Triple containing the First Name, Middle Name, and Last Name
        return "$firstName $middleName $lastName"
    }

    fun getStandardPhoneNumber(number: String):Boolean{

        return if (number.length > 8){
            val input1 = StringBuilder()
            input1.append(number)
            val reversedString = input1.reverse()
            val newReversedString = reversedString.substring(0, 9)

            val stringBuilder = StringBuilder()
            stringBuilder.append(newReversedString)
            val newString = stringBuilder.reverse()
            val newPhone= "0$newString"
            true
        }else{
            false
        }



    }


}