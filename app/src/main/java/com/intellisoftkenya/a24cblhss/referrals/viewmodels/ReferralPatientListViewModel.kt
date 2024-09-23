package com.intellisoftkenya.a24cblhss.referrals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.count
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.DbPatientItem
import com.intellisoftkenya.a24cblhss.shared.DbServiceRequest
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest

class ReferralPatientListViewModel(
    application: Application
) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    private var formatterClass = FormatterClass(application.applicationContext)

    private var fhirEngine: FhirEngine =
        FhirApplication.fhirEngine(application.applicationContext)

    val liveSearchedPatients = MutableLiveData<List<DbPatientItem>>()
    val patientCount = MutableLiveData<Long>()

    init {
        updatePatientListAndPatientCount({ getSearchResults("") }, { count() })
    }

    private fun updatePatientListAndPatientCount(
        search: suspend () -> List<DbPatientItem>,
        count: suspend () -> Long,
    ) {
        viewModelScope.launch {
            liveSearchedPatients.value = search()
            patientCount.value = count()
        }
    }

    private suspend fun count(nameQuery: String = ""): Long {
        return fhirEngine.count<Patient> {
            if (nameQuery.isNotEmpty()) {
                filter(
                    Patient.NAME,
                    {
                        modifier = StringFilterModifier.CONTAINS
                        value = nameQuery
                    },
                )
            }
        }
    }

    fun searchPatientsByName(nameQuery: String) {
        updatePatientListAndPatientCount({ getSearchResults(nameQuery) }, { count(nameQuery) })
    }

    private suspend fun getSearchResults(nameQuery: String = ""):
            ArrayList<DbPatientItem> {

        val dbPatientItemList = ArrayList<DbPatientItem?>()

        var patients: MutableList<DbPatientItem> = mutableListOf()

        fhirEngine
            .search<ServiceRequest> {}
            .mapIndexed { index, fhirPatient -> createServiceRequest(fhirPatient.resource) }
            .let { dbPatientItemList.addAll(it) }

        patients = dbPatientItemList.filterNotNull().toMutableList()


        return ArrayList(patients)
    }

    private suspend fun createServiceRequest(resource: ServiceRequest):DbPatientItem? {

        val id = resource.id.replace("ServiceRequest/","")

        val patientId = if (resource.hasSubject())
            resource.subject.referenceElement_
                .toString().replace("Patient/","")

        else ""
        val status = if (resource.hasStatus()) resource.status.toString() else ""
        val occurrenceDateTime = if (resource.hasOccurrenceDateTimeType()) resource.occurrenceDateTimeType.toString().replace("DateTimeType[", "") else null
        val supportingInfo = if (resource.hasSupportingInfo()) resource.supportingInfo else emptyList()
        val reasonCodeList = if (resource.hasReasonCode()) resource.reasonCode else emptyList()
        var isReferral = false
        var display = ""

        reasonCodeList.forEach {

            val text = if (it.hasText()) it.text else ""
            if (text == "REFERRAL_MODULE") isReferral = true
            if (text == "REASON_FOR_REFERRAL"){
                val coding = if (it.hasCoding()) it.codingFirstRep else null
                if (coding != null){
                    display = if (coding.hasDisplay()) coding.displayElement.toString() else ""
                }
            }
        }

        if (isReferral){

            val searchResult =
                fhirEngine.search<Patient> {
                    filter(Resource.RES_ID, { value = of(patientId) })
                }

            if (searchResult.isNotEmpty()){
                searchResult.first().let {
                    val patient = it.resource
                    //Name
                    var name = ""
                    if (patient.hasName()){
                        patient.name.forEach {humanName->
                            val tag = if (humanName.hasText()) humanName.text else ""
                            val text = if (humanName.hasGiven()){
                                humanName.givenAsSingleString
                            } else if (humanName.hasFamily()){
                                humanName.family
                            }else{
                                ""
                            }
                            name = "$name $text"

                        }
                    }
                    //DOB
                    var dob = ""
                    if (patient.hasBirthDateElement()) {
                        if (patient.birthDateElement.hasValue()) {
                            val birthDateElement = patient.birthDateElement.valueAsString
                            dob = birthDateElement.toString()
                        }
                    }
                    return DbPatientItem(
                        patientId,
                        name,
                        patientId.substring(0..6),
                        dob,
                        null
                    )

                }

            }

        }
        return null

    }

    class ReferralPatientListViewModelFactory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReferralPatientListViewModel::class.java)) {
                return ReferralPatientListViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}