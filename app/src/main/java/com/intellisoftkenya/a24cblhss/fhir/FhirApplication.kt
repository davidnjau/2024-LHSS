package com.intellisoftkenya.a24cblhss.fhir

import android.app.Application
import android.content.Context
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.remote.HttpLogger
import com.intellisoftkenya.a24cblhss.fhir.Constants.BASE_FHIR_URL
import com.intellisoftkenya.a24cblhss.fhir.Constants.BASE_URL
import timber.log.Timber


class FhirApplication : Application(), DataCaptureConfig.Provider {
    // Only initiate the FhirEngine when used for the first time, not when the app is created.
    private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

    private var dataCaptureConfig: DataCaptureConfig? = null

    private val dataStore by lazy { DemoDataStore(this) }

    override fun onCreate() {
        super.onCreate()
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        }
        Timber.plant(Timber.DebugTree())

        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = false,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration(
                    BASE_FHIR_URL,
                    httpLogger =
                    HttpLogger(
                        HttpLogger.Configuration(
//                            if (BuildConfig.DEBUG) HttpLogger.Level.BODY else HttpLogger.Level.BASIC
                            HttpLogger.Level.BODY
                        )
                    ) { Timber.tag("App-HttpLog").d(it) }
                )
            )
        )
        Sync.oneTimeSync<FhirSyncWorker>(this)

        dataCaptureConfig =
            DataCaptureConfig().apply {
                urlResolver = ReferenceUrlResolver(this@FhirApplication as Context)
                valueSetResolverExternal = object : ValueSetResolver() {}
                xFhirQueryResolver =
                    XFhirQueryResolver { fhirEngine.search(it).map { it.resource } }
//                    XFhirQueryResolver{fhirEngine.search(it)}

            }
    }

    private fun constructFhirEngine(): FhirEngine {
        return FhirEngineProvider.getInstance(this)
    }

    companion object {
        fun fhirEngine(context: Context) =
            (context.applicationContext as FhirApplication).fhirEngine

        fun dataStore(context: Context) = (context.applicationContext as FhirApplication).dataStore
    }

    override fun getDataCaptureConfig(): DataCaptureConfig =
        dataCaptureConfig ?: DataCaptureConfig()
}