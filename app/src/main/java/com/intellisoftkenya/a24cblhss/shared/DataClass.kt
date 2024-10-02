package com.intellisoftkenya.a24cblhss.shared

import androidx.annotation.DrawableRes
import com.intellisoftkenya.a24cblhss.R
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
enum class WorkflowTitles(
    @DrawableRes val iconId: Int,
    val textId: String,
    val text: String,
) {
    DEMOGRAPHICS(R.drawable.ic_action_demographics, DbClasses.DEMOGRAPHICS.name, "Demographics"),
    ADDRESS(R.drawable.ic_action_address, DbClasses.ADDRESS.name, "Address"),
    NEXT_OF_KIN(R.drawable.ic_action_kin, DbClasses.NEXT_OF_KIN.name,  "Next Of Kin"),

    REFERRING_FACILITY_INFO(R.drawable.ic_referring_facility_info, DbClasses.REFERRING_FACILITY_INFO.name,  "Referring Facility Information"),
    REFERRAL_INFO(R.drawable.ic_referrals, DbClasses.REFERRAL_INFO.name,  "Referral Information"),
    CLINICAL_REFERRAL_I(R.drawable.ic_action_next_kin, DbClasses.CLINICAL_REFERRAL_I.name,  "Clinical Referral I"),
    CLINICAL_REFERRAL_II(R.drawable.ic_action_next_kin, DbClasses.CLINICAL_REFERRAL_II.name,  "Clinical Referral II"),
    CLINICAL_REFERRAL_III(R.drawable.ic_action_next_kin, DbClasses.CLINICAL_REFERRAL_III.name,  "Clinical Referral III"),

    ACKNOWLEDGEMENT_FORM(R.drawable.ic_action_ackno, DbClasses.ACKNOWLEDGEMENT_FORM.name,  "Acknowledgement Form"),
}

