package com.av.ajouuniv.avproject2.data

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class HueSharedPreferences private constructor(appContext: Context) {
    private var mSharedPreferences: SharedPreferences? = null

    private var mSharedPreferencesEditor: Editor? = null


    val username: String
        get() {
            val username = mSharedPreferences!!.getString(LAST_CONNECTED_USERNAME, "")
            return username
        }

    val lastConnectedIPAddress: String
        get() = mSharedPreferences!!.getString(LAST_CONNECTED_IP, "")

    init {
        mSharedPreferences = appContext.getSharedPreferences(HUE_SHARED_PREFERENCES_STORE, 0) // 0 - for private mode
        mSharedPreferencesEditor = mSharedPreferences!!.edit()
    }

    fun setUsername(username: String): Boolean {
        mSharedPreferencesEditor!!.putString(LAST_CONNECTED_USERNAME, username)
        return mSharedPreferencesEditor!!.commit()
    }

    fun setLastConnectedIPAddress(ipAddress: String): Boolean {
        mSharedPreferencesEditor!!.putString(LAST_CONNECTED_IP, ipAddress)
        return mSharedPreferencesEditor!!.commit()
    }

    companion object {
        private val HUE_SHARED_PREFERENCES_STORE = "HueSharedPrefs"
        private val LAST_CONNECTED_USERNAME = "LastConnectedUsername"
        private val LAST_CONNECTED_IP = "LastConnectedIP"
        private var instance: HueSharedPreferences? = null

        fun getInstance(ctx: Context): HueSharedPreferences {
            if (instance == null) {
                instance = HueSharedPreferences(ctx)
            }
            return instance as HueSharedPreferences
        }
    }
}
