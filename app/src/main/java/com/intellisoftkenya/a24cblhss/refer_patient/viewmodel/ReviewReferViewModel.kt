package com.intellisoftkenya.a24cblhss.refer_patient.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientListViewModel
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun getReasonForReferral(formDataList: List<FormData>): String? {
        // Find the FormData with the title "REFERRAL_INFO"
        val referralInfo = formDataList.find { it.title == "REFERRAL_INFO" }

        // If found, find the DbFormData with the tag "Reason for Referral"
        val reasonForReferral = referralInfo?.formDataList?.find { it.tag == "Reason for Referral" }

        // Return the text (i.e., the value) of the "Reason for Referral", or null if not found
        return reasonForReferral?.text
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


        val reasonCodeList = ArrayList<CodeableConcept>()

        val referralReason = getReasonForReferral(formDataList)
        if (referralReason != null){
            val codeableConcept = CodeableConcept()
            codeableConcept.text = "REASON_FOR_REFERRAL"

            val codingList = ArrayList<Coding>()

            val coding = Coding()
            coding.id = formatterClass.generateUuid()
            coding.display = referralReason
            coding.code = generateRandomLoincCode()
            codingList.add(coding)

            codeableConcept.coding = codingList
            codeableConcept.id = formatterClass.generateUuid()

            reasonCodeList.add(codeableConcept)
        }

        val codeableConcept = CodeableConcept()
        codeableConcept.text = "REFERRAL_MODULE"
        codeableConcept.id = formatterClass.generateUuid()
        reasonCodeList.add(codeableConcept)


        serviceRequest.reasonCode = reasonCodeList

        serviceRequest.occurrence = DateTimeType.now()
        serviceRequest.supportingInfo = ArrayList()

        // Iterate through formDataList and create Encounters and Observations
        formDataList.forEach { formData ->
            // Create an Encounter based on the title
            val encounter = createEncounter(
                formData.title,
                patientId,
                serviceRequest.id,
                null,
                Encounter.EncounterStatus.FINISHED)

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

    fun createClinicalInfo(formDataList: ArrayList<FormData>, workflowTitles: String){
        CoroutineScope(Dispatchers.IO).launch {
            createClinicalInfoBac(formDataList, workflowTitles)
        }
    }

    private suspend fun createClinicalInfoBac(
        formDataList: ArrayList<FormData>,
        workflowTitles: String) {

        val patientId = formatterClass.getSharedPref("", "patientId")
        val carePlan = formatterClass.getSharedPref("", "carePlanId")

        if (patientId != null && carePlan != null) {

            // Iterate through formDataList and create Encounters and Observations
            formDataList.forEach { formData ->
                // Create an Encounter based on the title
                val encounter = createEncounter(
                    workflowTitles,
                    patientId,
                    null,
                    carePlan,
                    Encounter.EncounterStatus.PLANNED
                )

                saveResourceToDatabase(encounter, "Encounter")

                // Create Observations from formDataList and link them to the Encounter
                formData.formDataList.forEach { dbFormData ->
                    val observation = createObservation(
                        dbFormData, patientId, encounter.id)
                    saveResourceToDatabase(observation, "Observation")
                }
                // Add Observation to supportingInfo of ServiceRequest
            }


        }


    }

    // Function to create an Encounter
    private fun createEncounter(
        title: String,
        patientId: String,
        serviceRequestId: String?,
        carePlanId: String?,
        status: Encounter.EncounterStatus,
    ): Encounter {
        val encounter = Encounter()
        encounter.id =  formatterClass.generateUuid()  // Use timestamp as a unique ID
        encounter.subject = Reference("Patient/$patientId")
        encounter.reasonCode = listOf(CodeableConcept().setText(title))

        encounter.status = status

        val basedOnList = ArrayList<Reference>()
        if (serviceRequestId != null) {
            basedOnList.add(Reference("ServiceRequest/$serviceRequestId"))
        }
        if (carePlanId!= null) {
            basedOnList.add(Reference("CarePlan/$carePlanId"))
        }
        encounter.basedOn = basedOnList


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