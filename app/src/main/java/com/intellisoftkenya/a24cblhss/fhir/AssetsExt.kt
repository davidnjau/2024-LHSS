package com.intellisoftkenya.a24cblhss.fhir


import android.content.Context

fun Context.readFileFromAssets(fileName: String): String =
    assets.open(fileName).bufferedReader().use { it.readText() }