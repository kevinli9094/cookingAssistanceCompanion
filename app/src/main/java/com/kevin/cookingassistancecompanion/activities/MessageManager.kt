package com.kevin.cookingassistancecompanion.activities

import android.content.Context
import android.widget.Toast

class MessageManager(
    private val context: Context
) {

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }
}