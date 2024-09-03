package com.example.InMyFridge.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val utilContext = context



    fun getString(preferences: SharedPreferences, key: String, defValue: String): String {
        return preferences.getString(key, defValue).toString()
    }

    fun setString(preferences: SharedPreferences, key: String, Value: String) {
        preferences.edit().putString(key, Value).apply()
    }
    fun getBoolean(preferences: SharedPreferences, key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }
    fun setBoolean(preferences: SharedPreferences, key: String, Value: Boolean) {
        preferences.edit().putBoolean(key, Value).apply()
    }

    fun getPreferences(prefName: String): SharedPreferences {
        val preferences: SharedPreferences =
            utilContext.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        return preferences
    }
}