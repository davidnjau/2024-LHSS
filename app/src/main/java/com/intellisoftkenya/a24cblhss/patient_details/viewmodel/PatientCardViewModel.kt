package com.intellisoftkenya.a24cblhss.patient_details.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.shared.DbClasses
import com.intellisoftkenya.a24cblhss.shared.DbFormData
import com.intellisoftkenya.a24cblhss.shared.FormData
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource

class PatientCardViewModel(
    application: Application,
    private val fhirEngine: FhirEngine,
    private val patientId: String,
) : AndroidViewModel(application) {

    fun getPatientInfo() = runBlocking {
        getPatientDetailDataModel()
    }

    private suspend fun getPatientDetailDataModel(): ArrayList<FormData> {

        val formDataList = ArrayList<FormData>()

        val searchResult =
            fhirEngine.search<Patient> {
                filter(Resource.RES_ID, { value = of(patientId) })
            }

        if (searchResult.isNotEmpty()){
            searchResult.first().let {
                val patient = it.resource

                /**
                 * 1. Demographics
                 */
                val demographicFormDataList =  ArrayList<DbFormData>()

                //Name
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
                        if (tag != "" && text != ""){
                            demographicFormDataList.add(
                                DbFormData(tag, text)
                            )
                        }

                    }
                }

                //Gender
                if (patient.hasGenderElement()) {
                    val gender = it.resource.genderElement.valueAsString
                    demographicFormDataList.add(
                        DbFormData("Sex", gender)
                    )
                }

                //Phone
                if (patient.hasTelecom()) {
                    if (it.resource.telecom.isNotEmpty()) {
                        if (it.resource.telecom.first().hasValue()) {
                            val phone = it.resource.telecom.first().value
                            demographicFormDataList.add(
                                DbFormData("Telephone", phone)
                            )
                        }
                    }
                }

                //DOB
                if (patient.hasBirthDateElement()) {
                    if (patient.birthDateElement.hasValue()) {
                        val birthDateElement = patient.birthDateElement.valueAsString
                        demographicFormDataList.add(
                            DbFormData("Date of Birth", birthDateElement)
                        )
                    }
                }

                formDataList.add(
                    FormData(
                        DbClasses.DEMOGRAPHICS.name,
                        demographicFormDataList
                    )
                )

                /**
                 * 2. Next Of Kin
                 */
                val kinFormDataList =  ArrayList<DbFormData>()

                if (patient.hasContact()) {
                    patient.contact.forEach { k ->

                        val name = if (k.hasName()) {
                            k.name.nameAsSingleString.toString()
                        } else ""
                        val phone = if (k.hasTelecom()) k.telecomFirstRep.value else ""
                        val rshp = if (k.hasRelationship()) k.relationshipFirstRep.text else ""

                        kinFormDataList.add(
                            DbFormData("Full Name", name)
                        )
                        kinFormDataList.add(
                            DbFormData("Phone Number", phone)
                        )
                        kinFormDataList.add(
                            DbFormData("Relationship", rshp)
                        )


                    }
                }
                formDataList.add(
                    FormData(
                        DbClasses.NEXT_OF_KIN.name,
                        kinFormDataList
                    )
                )

                /**
                 * 3. Address Information
                 */
                val addressFormDataList =  ArrayList<DbFormData>()

                if (patient.hasAddress()) {
                    if (patient.addressFirstRep.hasCountry()){
                        addressFormDataList.add(
                            DbFormData("Country", patient.addressFirstRep.countryElement.valueAsString)
                        )
                    }
                    if (patient.addressFirstRep.hasState()){
                        addressFormDataList.add(
                            DbFormData("County", patient.addressFirstRep.stateElement.valueAsString)
                        )
                    }
                    if (patient.addressFirstRep.hasCity()){
                        addressFormDataList.add(
                            DbFormData("Sub County", patient.addressFirstRep.cityElement.valueAsString)
                        )
                    }
                }
                formDataList.add(
                    FormData(
                        DbClasses.ADDRESS.name,
                        addressFormDataList
                    )
                )

            }

        }


        return formDataList
    }


}

class PatientDetailsViewModelFactory(
    private val application: Application,
    private val fhirEngine: FhirEngine,
    private val patientId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PatientCardViewModel::class.java)) {
            "Unknown ViewModel class"
        }
        return PatientCardViewModel(application, fhirEngine, patientId) as T
    }
}