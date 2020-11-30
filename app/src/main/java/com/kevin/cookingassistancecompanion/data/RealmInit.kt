package com.kevin.cookingassistancecompanion.data

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmInit {
    fun init(context: Context) {
        Realm.init(context)
        val realmConfig = RealmConfiguration.Builder()
//            .assetFile("default.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}