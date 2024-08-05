package com.example.kocoatalk

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SignupMailActivity : AppCompatActivity() {

    private lateinit var btn_auth: Button
    private lateinit var btn_next: Button
    private lateinit var edt_email: EditText
    private lateinit var edt_authnum: EditText
    private lateinit var txt_alert: TextView
    private lateinit var authcode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_mail)

        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        btn_auth = findViewById(R.id.btn_signup_auth_req)
        btn_next = findViewById(R.id.btn_signup_netx)
        edt_email = findViewById(R.id.edt_signup_email)
        edt_authnum = findViewById(R.id.edt_signup_auth)
        txt_alert = findViewById(R.id.txt_signup_alert)
    }

    private fun setListeners() {
        edt_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed during text change
            }

            override fun afterTextChanged(s: Editable?) {
                val isValid = isValidEmail(s.toString())
                btn_auth.isEnabled = isValid
                if (isValid) {
                    btn_auth.setBackgroundColor(
                        ContextCompat.getColor(
                            this@SignupMailActivity,
                            R.color.green
                        )
                    )
                } else {
                    btn_auth.setBackgroundColor(
                        ContextCompat.getColor(
                            this@SignupMailActivity,
                            R.color.gray
                        )
                    )
                }
            }
        })

        btn_auth.setOnClickListener {
            sendEmail(edt_email.text.toString())
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmail(address: String) {
        val emailAddress = address
        val title = "Kocaotalk 가입 인증 메일입니다"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // 이메일 앱에서만 인텐트 처리되도록 설정
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress)) // 메일 수신 주소 목록
            putExtra(Intent.EXTRA_SUBJECT, title) // 메일 제목 설정
            authcode = createNumberCode()
            putExtra(Intent.EXTRA_TEXT, "인증 코드: $authcode") // 메일 본문 설정
        }

        // 로그 추가
        Log.d("SignupMailActivity", "Intent to send email: $intent")

        // Ensure that the package manager is not null and can resolve the intent
        if (packageManager?.resolveActivity(intent, 0) != null) {
            Log.d("SignupMailActivity", "Email client found.")
            startActivity(Intent.createChooser(intent, "메일 전송하기"))
        } else {
            Log.d("SignupMailActivity", "No email client found.")
            Toast.makeText(this, "메일을 전송할 수 없습니다", Toast.LENGTH_LONG).show()
        }
    }

    private fun createNumberCode(): String { // 이메일 인증 코드 생성
        val str = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        var newCode = ""
        for (x in 0 until 6) {
            val random = (Math.random() * str.size).toInt()
            newCode += str[random]
        }
        return newCode
    }
}