package com.intellisoftkenya.a24cblhss.shared

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.intellisoftkenya.a24cblhss.R

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class FormatterClass(private val context: Context) {

    fun generateUuid(): String {
        return UUID.randomUUID().toString()
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



}