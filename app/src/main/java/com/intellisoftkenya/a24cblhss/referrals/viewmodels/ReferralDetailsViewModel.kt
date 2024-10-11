package com.intellisoftkenya.a24cblhss.referrals.viewmodels

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
import com.intellisoftkenya.a24cblhss.patient_details.viewmodel.PatientCardViewModel
import com.intellisoftkenya.a24cblhss.shared.DbEncounter
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.FormData
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest

class ReferralDetailsViewModel(
    application: Application,
    private val fhirEngine: FhirEngine,
    private val patientId: String,
    private val serviceRequestId: String,
) : AndroidViewModel(application) {

    // LiveData to expose the list of items
    private val _clinicalLiveData = MutableLiveData<List<FormData>>()
    val clinicalLiveData: LiveData<List<FormData>> = _clinicalLiveData

    // LiveData to handle loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val formatterClass = FormatterClass(application.applicationContext)

    fun getClinicalList(): ArrayList<FormData> {
        val items = ArrayList<FormData>()

        viewModelScope.launch {
            _isLoading.value = true
            try {

                val encounter = formatterClass.getSharedPref("", "encounterId")
                if (encounter != null) {

                    //Make sure the encounter is in the form of 'Encounter/...'
                    val dbFormData = getEncounterDetails(encounter)
                    if (dbFormData != null) {
                        items.add(dbFormData)
                    }
                }
                _clinicalLiveData.value = items
            } catch (e: Exception) {
                // Handle error (e.g., log it, or show an error message)
            } finally {
                _isLoading.value = false
            }
        }
        return items
    }

    fun getServiceRequest() = runBlocking {
        getServiceRequestBac()
    }


    private suspend fun getServiceRequestBac(): ArrayList<FormData> {

        val formDataList = ArrayList<FormData>()

        val searchResult =
            fhirEngine.search<ServiceRequest> {
                filter(Resource.RES_ID, { value = of(serviceRequestId) })
            }

        if (searchResult.isNotEmpty()) {
            searchResult.first().let {

                val encounterList = it.resource.supportingInfo
                encounterList.forEach { encounterDetails ->

                    val encounter = encounterDetails.reference

                    val dbFormData = getEncounterDetails(encounter)
                    if (dbFormData != null){
                        formDataList.addAll(listOf(dbFormData))
                    }

                }

            }
        }
        return formDataList

    }

    private fun createObservationItem(resource: Observation):DbFormData {

        val tag = if(resource.hasCode() && resource.code.hasCoding()){
            resource.code.codingFirstRep.display
        }else ""

        val text = if (resource.hasValueStringType()){
            resource.valueStringType.valueAsString
        }else ""

        return DbFormData(
            tag, text
        )

    }


    private suspend fun getEncounterDetails(encounter:String):FormData?{

        val observationList = ArrayList<DbFormData>()
        var title = ""
        val encounterId = encounter.replace("Encounter/","")

        fhirEngine
            .search<Observation> {
                filter(Observation.SUBJECT, { value = "Patient/$patientId" })
                filter(Observation.ENCOUNTER, { value = encounter })
                sort(Observation.DATE, Order.ASCENDING)
            }
            .map { createObservationItem(it.resource) }
            .let {observationList.addAll(it)}

        val searchResult =
            fhirEngine.search<Encounter> {
                filter(Resource.RES_ID, { value = of(encounterId) })
            }

        if (searchResult.isNotEmpty()) {
            searchResult.first().let {
                title = if (it.resource.hasReasonCode() && it.resource.reasonCodeFirstRep.hasText()){
                    it.resource.reasonCodeFirstRep.text
                }else ""
            }
        }

        if (title != "" && observationList.isNotEmpty()){
            val formData = FormData(
                title,
                observationList
            )
            return formData
        }
        return null
    }

    fun getEncounterList() = runBlocking {
        getEncounterListBac()
    }

    private suspend fun getEncounterListBac(): ArrayList<DbEncounter> {

        val formDataList = ArrayList<DbEncounter>()

        fhirEngine
            .search<Encounter> {
                filter(Encounter.SUBJECT, { value = "Patient/$patientId" })
                sort(Encounter.DATE, Order.ASCENDING)
            }
            .map { createEncounterItem(it.resource) }
            .let {formDataList.addAll(it)}

        return formDataList

    }

    private fun createEncounterItem(resource: Encounter): DbEncounter {

        val id = resource.id
        val status = resource.status.toString()

        val dateCreated = if (resource.hasPeriod() && resource.period.hasStart()) {
            resource.period.start.toString()
        }else ""
        val referralReason = if (resource.hasReasonCode()){
            resource.reasonCodeFirstRep.text
        }else ""

        return DbEncounter(
            id,
            dateCreated,
            status,
            "",
            referralReason)

    }


}
class ReferralDetailsViewModelFactory(
    private val application: Application,
    private val fhirEngine: FhirEngine,
    private val patientId: String,
    private val serviceRequestId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ReferralDetailsViewModel::class.java)) {
            "Unknown ViewModel class"
        }
        return ReferralDetailsViewModel(application, fhirEngine, patientId, serviceRequestId) as T
    }
}