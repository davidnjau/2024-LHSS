package com.intellisoftkenya.a24cblhss.clinical_info.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralPatientListViewModel
import com.intellisoftkenya.a24cblhss.shared.DbCarePlan
import com.intellisoftkenya.a24cblhss.shared.DbNavigationDetails
import com.intellisoftkenya.a24cblhss.shared.FormatterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.CarePlan.CarePlanStatus
import org.hl7.fhir.r4.model.Observation
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


    fun closeCarePlan(carePlanId: String) {
        val supportingInfo = ArrayList<Reference>()
        Log.e("******* ", "carePlanId: $carePlanId")
        updateCarePlanStatus(CarePlanStatus.COMPLETED, carePlanId, supportingInfo)
    }

    fun getCarePlanDetails() = runBlocking {
        getCarePlanDetailsBac()
    }

    private suspend fun getCarePlanDetailsBac(): ArrayList<DbCarePlan> {

        val carePlanList = ArrayList<DbCarePlan>()

        fhirEngine
            .search<CarePlan> {
                filter(Observation.SUBJECT, { value = "Patient/$patientId" })
                sort(Observation.DATE, Order.ASCENDING)
            }
            .map { createCarePlanItem(it.resource) }
            .let {carePlanList.addAll(it)}

        val activeCarePlans = carePlanList.filterActive("ACTIVE")
        val completedCarePlans = carePlanList.filterActive("COMPLETED")

        //Combine active and completed care plans into a single list
        val combinedCarePlans = activeCarePlans + completedCarePlans

        return ArrayList(combinedCarePlans)

    }

    private fun List<DbCarePlan>.filterActive(status: String): List<DbCarePlan> {
        return this.filter { it.status == status }
    }

    private fun createCarePlanItem(resource: CarePlan): DbCarePlan {

        val id = resource.id
        val date = resource.created.toString()
        val status = resource.status.toString()
        val supportingInfoList = resource.supportingInfo

        val dateCreated = formatterClass.convertDateFormat(date) ?: ""

        return DbCarePlan(
            id,
            status,
            "",
            dateCreated,
            ArrayList(supportingInfoList)
        )

    }

    fun createCarePlan(){
        CoroutineScope(Dispatchers.IO).launch {
            createCarePlanBac()
        }
    }

    fun updateCarePlanStatus(
        status: CarePlanStatus,
        carePlanId: String,
        supportingInfoList: ArrayList<Reference>){

        CoroutineScope(Dispatchers.IO).launch {
            Log.e("*******2 ", "carePlanId: $carePlanId")

            updateCarePlanStatusBac(status,carePlanId, supportingInfoList)
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
        carePlan.status = CarePlanStatus.DRAFT
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

    private suspend fun updateCarePlanStatusBac(
        status: CarePlanStatus,
        carePlanId: String,
        supportingInfoList: ArrayList<Reference>){
        Log.e("*******3 ", "carePlanId: $carePlanId")

        Log.e("----Upd", "----$carePlanId")

        val searchResult =
            fhirEngine.search<CarePlan> {
                filter(Resource.RES_ID, { value = of(carePlanId) })
            }
        println("Search result: $searchResult")
        println("status: $status")

        if (searchResult.isNotEmpty()) {
            searchResult.first().let {
                val carePlan = it.resource
                carePlan.status = status

                println("carePlan: ${carePlan.status} - ${carePlan.id} - ${carePlan.title} - ${carePlan.description} - ${carePlan.created} - ${carePlan.subject}")

                supportingInfoList.forEach { supportingInfo ->
                    carePlan.supportingInfo.add(supportingInfo)
                }

                updateResourceToDatabase(carePlan, "CarePlan")
            }

        }
        Log.e("----Upd", "----$carePlanId")

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