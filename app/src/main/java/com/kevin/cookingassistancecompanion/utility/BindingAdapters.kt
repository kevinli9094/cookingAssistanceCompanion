package com.kevin.cookingassistancecompanion.utility

import android.view.View
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(flag: Boolean) {
    this.visibility = if (flag) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("selectedValue")
fun Spinner.setSelectedValue(selectedValue: Any?) {
    setSpinnerValue(selectedValue)
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun Spinner.getSelectedValue(): Any? {
    return getSpinnerValue()
}

@BindingAdapter("entries")
fun Spinner.setEntries(entries: List<Any>?) {
    setSpinnerEntries(entries)
}

@BindingAdapter("selectedValueAttrChanged")
fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
    setSpinnerInverseBindingListener(inverseBindingListener)
}