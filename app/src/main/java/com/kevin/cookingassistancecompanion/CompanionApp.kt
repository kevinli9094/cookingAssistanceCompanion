package com.kevin.cookingassistancecompanion

import android.app.Application
import com.kevin.cookingassistancecompanion.data.RealmInit

/**
 * Application class of the app. The starting point of the app.
 * All the app initialization belongs here
 */
class CompanionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RealmInit().init(this)
    }
}