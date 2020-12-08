package com.kevin.cookingassistancecompanion.utility

import android.content.Context
import android.widget.Toast

/**
 * Class to show message to user
 */
class MessageManager(
    private val context: Context
) {

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }
}