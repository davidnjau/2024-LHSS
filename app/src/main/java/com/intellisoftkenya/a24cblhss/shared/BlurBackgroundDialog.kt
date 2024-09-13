package com.intellisoftkenya.a24cblhss.shared

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.intellisoftkenya.a24cblhss.R

class BlurBackgroundDialog(
    context: Context,
    value: String,
    fragment: Fragment,
    actionNavigation: Int,
) : Dialog(context) {

    private lateinit var formatterClass: FormatterClass
    private val valueText = value
    private val currentFragment = fragment
    private val nextNavigation = actionNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_blur_background)

        formatterClass = FormatterClass(context)

        // Set window attributes to cover the entire screen
        window?.apply {
            attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes?.height = WindowManager.LayoutParams.MATCH_PARENT

            // Make the dialog cover the status bar and navigation bar
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )

            setBackgroundDrawableResource(android.R.color.transparent) // Set a transparent background
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(R.color.colorPrimary)

        val closeMaterialButton = findViewById<MaterialButton>(R.id.closeMaterialButton)

        findViewById<TextView>(R.id.info_textview).apply {
            text = valueText
        }


        closeMaterialButton.setOnClickListener {
            dismiss()
            currentFragment.findNavController().navigate(nextNavigation)
        }


    }
}