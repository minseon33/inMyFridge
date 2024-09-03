package com.example.InMyFridge.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.InMyFridge.KocaotalkApplication
import com.example.InMyFridge.R
import com.example.InMyFridge.Utils.PreferenceUtil

class SplashActivity : AppCompatActivity() {

    private lateinit var prefUtil: PreferenceUtil
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initializePreferences()
        navigateBasedOnAppState()
    }

    private fun initializePreferences() {
        prefUtil = PreferenceUtil(this)
        pref = prefUtil.getPreferences("pref_logedin_user")
    }

    private fun navigateBasedOnAppState() {
        val app = application as KocaotalkApplication
        val isLoggedIn = prefUtil.getBoolean(pref, "logged in", false)

        if (app.isAppRunning) {
            navigateToAppropriateScreen(isLoggedIn)
        } else {
            handleFirstAppLaunch(isLoggedIn)
        }
    }

    private fun navigateToAppropriateScreen(isLoggedIn: Boolean) {
        val destination = if (isLoggedIn) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, destination))
        finish()
    }

    private fun handleFirstAppLaunch(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            navigateToMainAfterSettingAppState()
        } else {
            showSplashScreenAndNavigate()
        }
    }

    private fun navigateToMainAfterSettingAppState() {
        val app = application as KocaotalkApplication
        app.isAppRunning = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSplashScreenAndNavigate() {
        Handler(Looper.getMainLooper()).postDelayed({
            val app = application as KocaotalkApplication
            app.isAppRunning = true
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000) // 2초
    }
}
