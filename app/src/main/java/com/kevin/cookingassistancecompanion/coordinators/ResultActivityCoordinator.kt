package com.kevin.cookingassistancecompanion.coordinators

import android.app.Activity
import android.content.Intent
import com.kevin.cookingassistancecompanion.activities.StoreSelectionActivity

/**
 * Coordinator for result screen
 */
class ResultActivityCoordinator(private val activity: Activity) {
    fun openSelectStoreActivity() {
        val intent = Intent(activity, StoreSelectionActivity::class.java)
        activity.startActivity(intent)
    }
}