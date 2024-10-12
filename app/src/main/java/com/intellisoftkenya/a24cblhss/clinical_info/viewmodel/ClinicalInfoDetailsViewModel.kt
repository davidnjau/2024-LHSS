package com.intellisoftkenya.a24cblhss.clinical_info.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralPatientListViewModel
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import java.util.Date

class ClinicalInfoDetailsViewModel(
    application: Application,
    private val patientId: String,
) : AndroidViewModel(application) {

    private var formatterClass = FormatterClass(application.applicationContext)

    private var fhirEngine: FhirEngine =
        FhirApplication.fhirEngine(application.applicationContext)


    fun createCarePlan(){
        CoroutineScope(Dispatchers.IO).launch {
            createCarePlanBac()
        }
    }

    fun updateCarePlanStatus(carePlanId: String, supportingInfoList: ArrayList<Reference>){
        CoroutineScope(Dispatchers.IO).launch {
            updateCarePlanStatusBac(carePlanId, supportingInfoList)
        }
    }

    private suspend fun createCarePlanBac(){

        /**
         * 1. Create a new CarePlan resource, with a status of Draft
         * 2. Once a clinical information is entered, update the status to active
         */

        val id = formatterClass.generateUuid()

        val carePlan = CarePlan()
        carePlan.id = id
        carePlan.status = CarePlan.CarePlanStatus.DRAFT
        carePlan.created = Date()
        carePlan.subject = Reference("Patient/$patientId")
        carePlan.title = "Clinical Information"
        carePlan.description = "Clinical information for patient: $patientId"
        saveResourceToDatabase(carePlan, "CarePlan")

        formatterClass.saveSharedPref(
            sharedPrefName = DbNavigationDetails.CARE_PLAN.name,
            "carePlanId",
            id
        )

    }

    private suspend fun updateCarePlanStatusBac(carePlanId: String,
                                                supportingInfoList: ArrayList<Reference>){

        val searchResult =
            fhirEngine.search<CarePlan> {
                filter(Resource.RES_ID, { value = of(carePlanId) })
            }

        if (searchResult.isNotEmpty()) {
            searchResult.first().let {
                val carePlan = it.resource
                carePlan.status = CarePlan.CarePlanStatus.ACTIVE

                supportingInfoList.forEach { supportingInfo ->
                    carePlan.supportingInfo.add(supportingInfo)
                }

                updateResourceToDatabase(carePlan, "CarePlan")
            }

        }
    }

    private suspend fun updateResourceToDatabase(resource: Resource, s: String) {
        Log.e("----Upd", "----$s")
        fhirEngine.update(resource)
    }

    private suspend fun saveResourceToDatabase(resource: Resource, type: String) :List<String>{
        Log.e("----", "----$type")
        return fhirEngine.create(resource)
    }

    class ClinicalInfoDetailsViewModelFactory(
        private val application: Application,
        private val patientId: String

    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClinicalInfoDetailsViewModel::class.java)) {
                return ClinicalInfoDetailsViewModel(application, patientId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}