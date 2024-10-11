package com.intellisoftkenya.a24cblhss.clinical_info.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intellisoftkenya.a24cblhss.R
import com.intellisoftkenya.a24cblhss.dynamic_components.DefaultLabelCustomizer
import com.intellisoftkenya.a24cblhss.dynamic_components.FieldManager
import com.intellisoftkenya.a24cblhss.dynamic_components.FormUtils
import com.intellisoftkenya.a24cblhss.shared.DbField

class BottomNavigationDrawerFragmentWithWidgets(dbFieldList: List<DbField>) : BottomSheetDialogFragment() {

    private lateinit var fieldManager: FieldManager
    private val dbFieldList: List<DbField> by lazy { dbFieldList }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the custom layout (without BottomSheetDialog in XML)
        return inflater.inflate(R.layout.bottom_navigation_drawer_with_widgets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldManager = FieldManager(DefaultLabelCustomizer(), requireContext())

        // Get references to widgets and handle interactions
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val rootLayout: LinearLayout = view.findViewById(R.id.rootLayout)

        FormUtils.populateView(ArrayList(dbFieldList),
            rootLayout, fieldManager, requireContext())

        btnAdd.setOnClickListener {



            Toast.makeText(context, "Action 1 clicked", Toast.LENGTH_SHORT).show()
            dismiss() // Close drawer if necessary
        }

    }
}