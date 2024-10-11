package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intellisoftkenya.a24cblhss.R

class BottomNavigationDrawerFragmentWithWidgets : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the custom layout (without BottomSheetDialog in XML)
        return inflater.inflate(R.layout.bottom_navigation_drawer_with_widgets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to widgets and handle interactions
        val buttonAction1: Button = view.findViewById(R.id.button_action_1)
        val buttonAction2: Button = view.findViewById(R.id.button_action_2)
        val buttonAction3: Button = view.findViewById(R.id.button_action_3)

        buttonAction1.setOnClickListener {
            Toast.makeText(context, "Action 1 clicked", Toast.LENGTH_SHORT).show()
            dismiss() // Close drawer if necessary
        }

        buttonAction2.setOnClickListener {
            Toast.makeText(context, "Action 2 clicked", Toast.LENGTH_SHORT).show()
            dismiss() // Close drawer if necessary
        }

        buttonAction3.setOnClickListener {
            Toast.makeText(context, "Action 3 clicked", Toast.LENGTH_SHORT).show()
            dismiss() // Close drawer if necessary
        }
    }
}