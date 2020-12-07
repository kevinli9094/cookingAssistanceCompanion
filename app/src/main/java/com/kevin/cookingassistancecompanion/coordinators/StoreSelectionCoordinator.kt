package com.kevin.cookingassistancecompanion.coordinators

import android.app.Activity
import android.content.Intent
import com.kevin.cookingassistancecompanion.activities.ScanActivity

/**
 * Coordinator for store selection screen
 */
class StoreSelectionCoordinator(private val activity: Activity) {
    fun openScanActivity() {
        val intent = Intent(activity, ScanActivity::class.java)
        activity.startActivity(intent)
    }
}