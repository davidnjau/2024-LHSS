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
enum class DbNavigationDetails{
    PATIENT_REGISTRATION
}
enum class DbClasses{
    DEMOGRAPHICS,
    ADDRESS,
    NEXT_OF_KIN
}


data class FormData(
    val title: String,
    val formDataList: ArrayList<DbFormData>
)


