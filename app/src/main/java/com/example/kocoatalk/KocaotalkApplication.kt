package com.example.kocoatalk

import android.app.Application

class KocaotalkApplication : Application() {
    var isAppRunning: Boolean = false

    override fun onCreate() {
        super.onCreate()
        // 앱 초기화 코드
    }
}