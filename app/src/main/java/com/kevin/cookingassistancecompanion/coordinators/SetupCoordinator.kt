package com.kevin.cookingassistancecompanion.coordinators

import android.app.Activity
import android.content.Intent
import com.kevin.cookingassistancecompanion.activities.StoreSelectionActivity

class SetupCoordinator(private val activity: Activity) {
    fun openStoreSelectionActivity() {
        val intent = Intent(activity, StoreSelectionActivity::class.java)
        activity.startActivity(intent)
    }
}