package com.kevin.cookingassistancecompanion.data

import android.util.Log
import io.realm.Realm

/**
 * Wrapper for realm to ensure the Closable realm is always realm
 */
class RealmConnection {

    companion object {
        const val TAG = "RealmConnection"
    }

    fun <T : Any> fetch(queryFunction: (Realm) -> T, defaultValue: T): T {
        val realm = Realm.getDefaultInstance()

        return try {
            queryFunction(realm)
        } catch (e: Throwable) {
            Log.e(TAG, e.stackTraceToString())
            defaultValue
        } finally {
            realm.close()
        }
    }

    fun execute(executeFunction: (Realm) -> Unit) {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction {
                executeFunction(it)
            }
        } catch (e: Throwable) {
            Log.e(TAG, e.stackTraceToString())
        } finally {
            realm.close()
        }
    }
}