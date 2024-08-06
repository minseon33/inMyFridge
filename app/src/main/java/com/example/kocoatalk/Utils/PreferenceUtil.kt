package com.example.kocoatalk.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val utilContext = context

    fun getString(preferences: SharedPreferences, key: String, defValue: String): String {
        return preferences.getString(key, defValue).toString()
    }

    fun setString(preferences: SharedPreferences, key: String, defValue: String) {
        preferences.edit().putString(key, defValue).apply()
    }

    fun getPreferences(prefName: String): SharedPreferences {
       val preferences: SharedPreferences = utilContext.getSharedPreferences("prefs_signup_auth", Context.MODE_PRIVATE)
        return preferences
    }
}