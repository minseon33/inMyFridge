package com.example.kocoatalk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class SignupMailActivity : AppCompatActivity() {

    private lateinit var btn_auth: Button
    private lateinit var btn_netx: Button
    private lateinit var edt_email: EditText
    private lateinit var edt_authnum: EditText
    private lateinit var txt_alert: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_mail)

        initializeViews()
        setlisteners()
    }

    private fun initializeViews() {
        btn_auth = findViewById(R.id.btn_signup_auth_req)
        btn_netx = findViewById(R.id.btn_signup_netx)

        edt_email = findViewById(R.id.btn_login)

        edt_authnum = findViewById(R.id.btn_kakaologin)
        txt_alert = findViewById(R.id.btn_naverlogin)
    }

    private fun setlisteners() {

        edt_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed during text change
            }

            override fun afterTextChanged(s: Editable?) {
                btn_auth.isEnabled = isValidEmail(s.toString())
            }


        })
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}