package com.intellisoftkenya.a24cblhss.shared

import org.hl7.fhir.r4.model.Reference

data class DbField(
    val widgets: String,
    val label: String,
    val isMandatory: Boolean = false,

    val inputType: Int? = null, // InputType for EditText (e.g., InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    val optionList : List<String> = emptyList()
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
    PATIENT_REGISTRATION,
    REFER_PATIENT,
    REFERRALS
}
enum class DbClasses{
    DEMOGRAPHICS,
    ADDRESS,
    NEXT_OF_KIN,

    REFERRING_FACILITY_INFO,
    REFERRAL_INFO,
    CLINICAL_REFERRAL_I,
    CLINICAL_REFERRAL_II,
    CLINICAL_REFERRAL_III,

    ACKNOWLEDGEMENT_FORM
}
data class Item(
    val title: String,
    val description: String,
    val imageResource: Int // Assuming image is a drawable resource
)


data class FormData(
    val title: String,
    val formDataList: ArrayList<DbFormData>
)

data class DbFormData(
    val tag: String,
    val text: String,
)
data class DbPatientItem(
    val id: String,
    val name: String,
    val crossBorderId:String,
    val dob:String,
    val dateCreated: String?
)
data class DbServiceRequest(
    val id:String,
    val patientId: String,
    val status: String,
    val dateRecorded: String,
    val encounterList: ArrayList<Reference>,
    val referralReason: String
)

