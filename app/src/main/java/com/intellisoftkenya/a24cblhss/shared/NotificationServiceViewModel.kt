package com.intellisoftkenya.a24cblhss.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import com.intellisoftkenya.a24cblhss.fhir.Constants
import com.intellisoftkenya.a24cblhss.fhir.FhirApplication
import com.intellisoftkenya.a24cblhss.referrals.viewmodels.ReferralDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.codesystems.EventStatus
import java.util.Date

class NotificationServiceViewModel(
    application: Application,
): AndroidViewModel(application) {

    private val formatterClass = FormatterClass(application.applicationContext)

    private var fhirEngine: FhirEngine =
        FhirApplication.fhirEngine(application.applicationContext)

    fun createNotification(dbCommunication: DbCommunication) {

        CoroutineScope(Dispatchers.IO).launch {

            // Create a reason code for the navigation
            val navigationCodeableConcept = CodeableConcept()
            navigationCodeableConcept.text = "ACTION"
            navigationCodeableConcept.id = formatterClass.generateUuid()
            navigationCodeableConcept.coding = arrayListOf(
                Coding(
                    Constants.SYSTEM_TB_REGISTRATION,
                    Constants.TOPIC_NUMBER_REASON_C0DE,
                    "${dbCommunication.navigationId}"
                )
            )

            //Build a communication
            val communication = Communication()

            val topicCodeableConcept = CodeableConcept()
            topicCodeableConcept.text = dbCommunication.title
            topicCodeableConcept.id = formatterClass.generateUuid()
            topicCodeableConcept.coding = arrayListOf(
                Coding(
                    Constants.SYSTEM_TB_REGISTRATION,
                    Constants.TOPIC_NUMBER,
                    dbCommunication.content
                )
            )

            communication.id = formatterClass.generateUuid()
            communication.subject = dbCommunication.subject
            communication.recipient = arrayListOf(dbCommunication.recipient)
            communication.sender = dbCommunication.sender
            communication.status = dbCommunication.status
            communication.priority = Communication.CommunicationPriority.ROUTINE
            communication.basedOn = arrayListOf(dbCommunication.basedOn)
            communication.sent = Date()
            communication.topic = topicCodeableConcept
            communication.reasonCode = arrayListOf(navigationCodeableConcept)

            fhirEngine.create(communication)

        }

    }

    fun getCommunicationList() = runBlocking {
        getCommunication()
    }

    private suspend fun getCommunication(): ArrayList<DbCommunicationData>{

        val communicationList = ArrayList<DbCommunicationData>()

        fhirEngine
            .search<Communication> {
                sort(Encounter.DATE, Order.ASCENDING)
            }
            .map { createCommunicationItem(it.resource) }
            .let {communicationList.addAll(it)}

        val notificationSize = communicationList.size

        formatterClass.saveSharedPref(
            "",
            "notificationSize",
            notificationSize.toString())


        return ArrayList(communicationList)
    }

    private fun createCommunicationItem(resource: Communication): DbCommunicationData {

        var patientId: String = ""
        var navigationId: Int = 0
        var title: String = ""
        var content: String = ""
        var dateTime: String = ""
        var status: String = ""
        var basedOn: String = ""

        val id = resource.id

        //1. This is the Navigation Id
        if (resource.hasReasonCode()){
            if (resource.hasText()){
                val text = resource.reasonCode[0].text
                if (text == "ACTION"){
                    if (resource.reasonCode[0].hasCoding()){
                        if (resource.reasonCode[0].coding[0].hasDisplay()){
                            val reasonCode = resource.reasonCode[0].coding[0].display
                            navigationId = reasonCode.toInt()
                        }
                    }
                }
            }
        }

        //2. This is the Patient Id
        if (resource.hasSubject()){
            val subjectReference = resource.subject.reference
            patientId = subjectReference
        }

        //3. This is the Notification content
        if (resource.hasTopic()){
            if (resource.hasTopic()){
                if (resource.topic.hasText()){
                    //This is the Notification title
                    val text = resource.topic.text
                    title = text

                }
                if (resource.topic.hasCoding()){
                    //This is the Notification content
                    val coding = resource.topic.coding[0].display
                    content = coding
                }
            }
        }

        //4. This is the Date of the Notification
        if (resource.hasSent()){
            val sent = resource.sent
            dateTime = sent.toString()
        }

        //5. This is the Notification status
        if (resource.hasStatus()){
            status = resource.status.toString()
        }

        val dateCreated = formatterClass.convertDateFormat(dateTime) ?: ""

        //6. This is the Notification basedOn
        if (resource.hasBasedOn()){
            val basedOnReference = resource.basedOn[0].reference
            basedOn = basedOnReference
        }

//        val annotationList = ArrayList<DbAnnotation>()



        return DbCommunicationData(
            id,
            patientId,
            navigationId,
            title,
            content,
            dateCreated,
            status,
            basedOn
        )

    }

}