package com.example.kocoatalk

import android.app.Application
import android.content.SharedPreferences
import com.example.kocoatalk.Utils.PreferenceUtil

class KocaotalkApplication : Application() {
    var isAppRunning: Boolean = false

    private lateinit var prefUtil: PreferenceUtil
    private lateinit var pref: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // 앱 초기화 코드
        prefUtil = PreferenceUtil(this)
        pref = prefUtil.getPreferences("pref_logedin_user")
    }
}