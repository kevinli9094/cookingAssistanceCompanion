package com.kevin.cookingassistancecompanion

import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.BindingAdapter

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

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(flag: Boolean){
    this.visibility = if(flag){
        View.VISIBLE
    } else {
        View.GONE
    }
}