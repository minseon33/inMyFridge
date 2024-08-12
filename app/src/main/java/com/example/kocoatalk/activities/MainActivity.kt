package com.example.kocoatalk.activities


import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kocoatalk.R
import com.example.kocoatalk.Utils.PreferenceUtil

class MainActivity : AppCompatActivity() {


    private lateinit var preferenceUtil: PreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceUtil = PreferenceUtil(this)
        val pref: SharedPreferences = preferenceUtil.getPreferences("pref_logedin_user")
        Log.i(TAG, preferenceUtil.getString(pref, "email", ""))

    }
}