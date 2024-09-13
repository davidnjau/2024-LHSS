package com.intellisoftkenya.a24cblhss.registration

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.FormData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PatientRegistrationSummaryViewModel(
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    private var fhirEngine: FhirEngine =
        FhirApplication.fhirEngine(application.applicationContext)

    // Function to create FHIR Patient resource from FormDataList
    private fun createPatientResourceBac(formDataList: ArrayList<FormData>): Patient {
        val patient = Patient()

        formDataList.forEach { formData ->
            when (formData.title) {
                DbClasses.DEMOGRAPHICS.name -> {
                    val humanNameList = ArrayList<HumanName>()
                    formData.formDataList.forEach { dbFormData ->
                        when (dbFormData.tag) {
                            "First Name", "Middle Name", "Last Name", "NickName" -> {
                                val humanName = addNameToPatient(dbFormData)
                                humanNameList.add(humanName)
                            }
                            "Sex" -> {
                                patient.gender = mapGender(dbFormData.text)
                            }
                            "DOB" -> {
                                patient.birthDate = parseDate(dbFormData.text)
                            }
                        }
                    }
                    patient.name = humanNameList
                }
                DbClasses.ADDRESS.name -> {
                    val address = Address()
                    formData.formDataList.forEach { dbFormData ->
                        when (dbFormData.tag) {
                            "Country of Origin", "Country of Residence" -> {
                                address.country = dbFormData.text
                            }
                            "Region/Province/County" -> {
                                address.state = dbFormData.text
                            }
                            "Sub County", "Ward/Village" -> {
                                address.city = dbFormData.text
                            }
                        }
                    }
                    patient.addAddress(address)
                }
                DbClasses.NEXT_OF_KIN.name -> {
                    addNextOfKinToPatient(patient, formData.formDataList)
                }
            }
        }

        return patient
    }

    fun createPatientResource(formDataList: ArrayList<FormData>){
        CoroutineScope(Dispatchers.IO).launch {
            val patient = createPatientResourceBac(formDataList)
            saveResourceToDatabase(patient, "Patient")
        }
    }

    private suspend fun saveResourceToDatabase(resource: Resource, type: String) {

        Log.e("----", "----$type")
        fhirEngine.create(resource)

    }

    // Helper function to add name to Patient resource
    private fun addNameToPatient(dbFormData: DbFormData):HumanName {

        val humanName = HumanName()
        humanName.text = dbFormData.tag
        when (dbFormData.tag) {
            "First Name","Middle Name" -> {
                humanName.use = HumanName.NameUse.OFFICIAL
                humanName.addGiven(dbFormData.text)
            }
            "NickName" -> {
                humanName.use = HumanName.NameUse.NICKNAME
                humanName.addGiven(dbFormData.text)
            }
            "Last Name" -> {
                humanName.use = HumanName.NameUse.USUAL
                humanName.family = dbFormData.text
            }
        }
        return humanName
    }

    // Helper function to map gender
    private fun mapGender(genderText: String): Enumerations.AdministrativeGender {
        return when (genderText) {
            "Male" -> Enumerations.AdministrativeGender.MALE
            "Female" -> Enumerations.AdministrativeGender.FEMALE
            else -> Enumerations.AdministrativeGender.UNKNOWN
        }
    }

    // Helper function to parse date string
    private fun parseDate(dateText: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(dateText) ?: Date()
    }

    // Helper function to add Next of Kin to Patient resource
    private fun addNextOfKinToPatient(patient: Patient, nextOfKinList: ArrayList<DbFormData>) {
        val contactComponent = Patient.ContactComponent()
        var name = HumanName()

        nextOfKinList.forEach { dbFormData ->
            when (dbFormData.tag) {
                "Full Name" -> {
                    name.family = dbFormData.text
                }
                "Relationship" -> {
                    contactComponent.relationship = listOf(
                        // Use proper relationship code from FHIR (e.g., 'family')
                        CodeableConcept().apply {
                            text = dbFormData.text
                        }
                    )
                }
                "Telephone" -> {
                    contactComponent.addTelecom(ContactPoint().apply {
                        system = ContactPoint.ContactPointSystem.PHONE
                        value = dbFormData.text
                    })
                }
            }
        }

        contactComponent.name = name
        patient.addContact(contactComponent)
    }}