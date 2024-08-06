package com.example.kocoatalk.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import com.example.kocoatalk.KocaotalkApplication
import com.example.kocoatalk.R

class SplashActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 타이머가 끝나면 내부 실행
        val app = application as KocaotalkApplication
        if (app.isAppRunning) {
            // 앱이 이미 실행 중인 경우 메인 화면으로 바로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // 앱이 처음 실행되는 경우 스플래시 화면을 보여줌
            setContentView(R.layout.activity_splash)
            Handler(Looper.getMainLooper()).postDelayed({
                // 앱의 MainActivity로 넘어가기
                app.isAppRunning = true
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                // 현재 액티비티 닫기
                finish()
            }, 2000) // 2초
        }
    }
}