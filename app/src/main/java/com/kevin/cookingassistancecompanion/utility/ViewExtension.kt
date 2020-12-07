package com.kevin.cookingassistancecompanion.utility

import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.InverseBindingListener

/**
 * set the entries for spinner
 */
fun Spinner.setSpinnerEntries(entries: List<Any>?) {
    if (entries != null) {
        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, entries)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter = arrayAdapter
    }
}

/**
 * set spinner value
 */
fun Spinner.setSpinnerValue(value: Any?) {
    if (adapter != null) {
        @Suppress("UNCHECKED_CAST")
        val position = (adapter as ArrayAdapter<Any>).getPosition(value)
        if(position >= 0){
            setSelection(position, false)
            tag = position
        }
    }
}

/**
 * get spinner value
 */
fun Spinner.getSpinnerValue(): Any? {
    return selectedItem
}

/**
 * set spinner onItemSelectedListener listener
 */
fun Spinner.setSpinnerInverseBindingListener(listener: InverseBindingListener?) {
    if (listener == null) {
        onItemSelectedListener = null
    } else {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (tag != position) {
                    listener.onChange()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                block()
            }
        }
    })
}