package com.example.test_lab_week_12

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

fun Spinner.setOnItemSelectedListener(listener: (parent: AdapterView<*>, view: View?, position: Int, id: Long) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View?,
            position: Int,
            id: Long
        ) {
            listener(parent, view, position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>) = Unit
    }
}
