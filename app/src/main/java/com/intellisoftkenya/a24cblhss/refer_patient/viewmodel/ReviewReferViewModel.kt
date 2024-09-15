package com.intellisoftkenya.a24cblhss.refer_patient.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.fhir.FhirEngine
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.StringType

class ReviewReferViewModel (
    application: Application,
    ): AndroidViewModel(application) {
    // TODO: Implement the ViewModel

        private var formatterClass = FormatterClass(application.applicationContext)

    private var fhirEngine: FhirEngine =
        FhirApplication.fhirEngine(application.applicationContext)

    fun createServiceRequest(formDataList: List<FormData>,
                             patientId: String,
                             requesterId: String?)= runBlocking {
        generateServiceRequest(formDataList,patientId, requesterId )
    }
    private suspend fun generateServiceRequest(
        formDataList: List<FormData>,
        patientId: String,
        requesterId: String?):List<String> {

        // Create a ServiceRequest
        val serviceRequest = ServiceRequest()
        serviceRequest.id = formatterClass.generateUuid()  // Set your unique ID
        serviceRequest.status = ServiceRequest.ServiceRequestStatus.ACTIVE
        serviceRequest.subject = Reference("Patient/$patientId")
        if (requesterId != null){
            serviceRequest.requester = Reference("Practitioner/$requesterId")
        }
        serviceRequest.reasonCode = listOf(
            CodeableConcept().setText("Referral Reason"))
        serviceRequest.occurrence = DateTimeType.now()
        serviceRequest.supportingInfo = ArrayList()

        // Iterate through formDataList and create Encounters and Observations
        formDataList.forEach { formData ->
            // Create an Encounter based on the title
            val encounter = createEncounter(formData.title, patientId, serviceRequest.id)
            serviceRequest.supportingInfo.add(Reference("Encounter/${encounter.id}"))
            saveResourceToDatabase(encounter, "Encounter")

            // Create Observations from formDataList and link them to the Encounter
            formData.formDataList.forEach { dbFormData ->
                val observation = createObservation(
                    dbFormData, patientId, encounter.id)
                saveResourceToDatabase(observation, "Observation")
            }
            // Add Observation to supportingInfo of ServiceRequest
        }

        return saveResourceToDatabase(serviceRequest, "ServiceRequest")
//        serviceRequest.note = listOf(Annotation().setText("ServiceRequest for referral created"))
    }

    private suspend fun saveResourceToDatabase(resource: Resource, type: String) :List<String>{
        Log.e("----", "----$type")
        return fhirEngine.create(resource)
    }


    // Function to create an Encounter
    private fun createEncounter(
        title: String,
        patientId: String,
        serviceRequestId: String
    ): Encounter {
        val encounter = Encounter()
        encounter.id =  formatterClass.generateUuid()  // Use timestamp as a unique ID
        encounter.status = Encounter.EncounterStatus.FINISHED
        encounter.subject = Reference("Patient/$patientId")
        encounter.reasonCode = listOf(CodeableConcept().setText(title))
        encounter.basedOn = listOf(Reference("ServiceRequest/$serviceRequestId"))

        return encounter
    }

    // Function to create an Observation
    private fun createObservation(
        dbFormData: DbFormData,
        patientId: String,
        encounterId: String): Observation {

        val observation = Observation()
        observation.id =  formatterClass.generateUuid() // Use timestamp as a unique ID
        observation.status = Observation.ObservationStatus.FINAL
        observation.subject = Reference("Patient/$patientId")

        // Generate a random LOINC code for the observation
        observation.code = CodeableConcept()
            .addCoding(
                Coding().setSystem("http://loinc.org")
                    .setCode(generateRandomLoincCode())
                    .setDisplay(dbFormData.tag)
            )

        // Set the encounter for the observation
        observation.encounter = Reference("Encounter/$encounterId")

        //set the value
        val type = StringType()
        type.id = formatterClass.generateUuid()
        type.value = dbFormData.text

        observation.value = type

        // Add the observation note from the form data
        val noteList = ArrayList<org.hl7.fhir.r4.model.Annotation>()
        val annotation = org.hl7.fhir.r4.model.Annotation()
        annotation.setText(dbFormData.tag)
        annotation.id = formatterClass.generateUuid()
        noteList.add(annotation)

        observation.note = noteList

        return observation
    }

    // Function to generate a random LOINC code (for example purposes)
    private fun generateRandomLoincCode(): String {
        val random = (10000..99999).random()
        return "LOINC-$random"
    }
}