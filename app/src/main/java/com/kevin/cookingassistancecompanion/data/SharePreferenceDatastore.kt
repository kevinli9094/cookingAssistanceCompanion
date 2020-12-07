package com.kevin.cookingassistancecompanion.data

import android.content.Context

/**
 * Datastore for user setting(eg: ip address and selected user)
 */
class SharePreferenceDatastore(private val context: Context) {
    companion object {
        private const val SHARE_PREFERENCE_NAME = "CookingAssistanceSharepreference"
        private const val KEY_SETUP = "KEY_SETUP"
        private const val KEY_URL = "KEY_IP_ADDRESS"
        private const val KEY_USER_ID = "KEY_USER_ID"
    }

    private val sharePreference =
        context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun isSetup(): Boolean {
        return sharePreference.getBoolean(KEY_SETUP, false)
    }

    fun setup() {
        val editor = sharePreference.edit()
        editor.putBoolean(KEY_SETUP, true)
        editor.apply()
    }

    fun updateUrl(url: String){
        val editor = sharePreference.edit()
        editor.putString(KEY_URL, url)
        editor.apply()
    }

    fun getUrl(): String{
        return sharePreference.getString(KEY_URL, "")!!
    }

    fun updateSelectedUser(userId: String){
        val editor = sharePreference.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getSelectedUserId(): String{
        return sharePreference.getString(KEY_USER_ID, "")!!
    }
}