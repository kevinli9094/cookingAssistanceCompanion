package com.kevin.cookingassistancecompanion

import android.app.Application
import com.kevin.cookingassistancecompanion.data.RealmInit

class CompanionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RealmInit().init(this)
    }
}