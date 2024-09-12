package com.intellisoftkenya.a24cblhss.shared

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.dynamic_components.DbFormData
import org.json.JSONObject

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.random.Random

class FormatterClass(private val context: Context) {

    fun saveSharedPref(key: String, value: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value);
        editor.apply();
    }

    fun getSharedPref(key: String): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        return sharedPreferences.getString(key, null)

    }


    fun deleteSharedPref(key: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
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
                // Continue to the next format if parsing fails
            }
        }

        // If none of the formats match, return an error message or handle it as needed
        return null
    }

    fun extractFormData(rootLayout: LinearLayout, viewModel: MainActivityViewModel) {

        // Traverse through all child views of rootLayout
        for (i in 0 until rootLayout.childCount) {
            when (val childView = rootLayout.getChildAt(i)) {

                is RadioGroup -> {
                    // Get the selected RadioButton ID
                    val selectedRadioButtonId = childView.checkedRadioButtonId
                    Log.e("----->", "$selectedRadioButtonId")

                    if (selectedRadioButtonId != -1) {
                        // Find the selected RadioButton using the selected ID
                        val selectedRadioButton = childView.findViewById<RadioButton>(selectedRadioButtonId)
                        val selectedText = selectedRadioButton.text.toString()

                        Log.e("----->selectedRadioButton", "$selectedRadioButton")
                        Log.e("----->selectedText", selectedText)


                        val tag = childView.tag
                        if (tag != null && selectedText.isNotEmpty()) {
                            val formData = DbFormData(tag.toString(), selectedText)

                            // Store form data in ViewModel
                            viewModel.updateFormData(tag.toString(), selectedText)
                        }
                    }
                }

                // Other view types like EditText, Spinner, etc.
                is EditText -> {
                    val tag = childView.tag
                    val text = childView.text.toString()

                    if (tag != null && text.isNotEmpty()) {
                        val formData = DbFormData(tag.toString(), text)

                        // Store form data in ViewModel
                        viewModel.updateFormData(tag.toString(), text)
                    }
                }
            }
        }
    }


}