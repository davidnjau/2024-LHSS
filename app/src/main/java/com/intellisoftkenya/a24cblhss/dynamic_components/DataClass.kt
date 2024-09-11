package com.intellisoftkenya.a24cblhss.dynamic_components

data class DbField(
    val widgets: String,
    val label: String,
    val isMandatory: Boolean = false,

    val inputType: Int? = null, // InputType for EditText (e.g., InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    val optionList : List<String> = emptyList()
)

data class DbFormData(
    val tag: String,
    val text: String,
)

data class DbCounty(
    val id: Int,
    val name: String
)
data class DbSubCounty(
    val id: Int,
    val name: String
)

enum class DbWidgets{
    EDIT_TEXT,
    SPINNER,
    RADIO_BUTTON,
    DATE_PICKER
}




enum class DbEditTextNames {
    FIRST_NAME_C, LAST_NAME_C, MIDDLE_NAME, OTHER_NAMES, TELEPHONE_C
}
enum class DbDOB {
    YEARS_C, MONTHS, DAYS
}
enum class IdentificationTypes {
    PASSPORT, NEMIS, NATIONAL_ID
}

data class FormData(
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val otherNames: String?,
    val telephone: String?,
    val years: Int?,
    val months: Int?,
    val days: Int?,
    val selectedDate: String?,
    val identificationType: IdentificationTypes?,
    val number: Int?
)

