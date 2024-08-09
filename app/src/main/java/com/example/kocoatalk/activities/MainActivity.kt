package com.example.kocoatalk.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kocoatalk.R

class MainActivity : AppCompatActivity() {

    private lateinit var txt_signup: TextView
    private lateinit var txt_findidpw: TextView
    private lateinit var btn_login: Button
    private lateinit var btn_kakoLogin: Button
    private lateinit var btn_naver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setOnclicks()
    }

    private fun initializeViews() {
        txt_signup = findViewById(R.id.ttv_signup)
        txt_findidpw = findViewById(R.id.ttv_findidpw)
        btn_login = findViewById(R.id.btn_login)
        btn_kakoLogin = findViewById(R.id.btn_kakaologin)
        btn_naver = findViewById(R.id.btn_naverlogin)
    }

    private fun setOnclicks() {
        txt_signup.setOnClickListener {
            val i = Intent(this@MainActivity, SignupMailActivity::class.java)

            startActivity(i)
        }

        // 다른 버튼 클릭 이벤트 추가
        btn_login.setOnClickListener {
            // 로그인 로직 추가
        }

        btn_kakoLogin.setOnClickListener {
            // 카카오 로그인 로직 추가
        }

        btn_naver.setOnClickListener {
            // 네이버 로그인 로직 추가
        }

        txt_findidpw.setOnClickListener {
            // 아이디/비밀번호 찾기 화면으로 이동
        }
    }


}