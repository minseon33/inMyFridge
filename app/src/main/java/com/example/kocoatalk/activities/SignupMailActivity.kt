package com.example.kocoatalk.activities

import android.content.Intent
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
import com.example.kocoatalk.R
import com.example.kocoatalk.Utils.GmailSender
import javax.mail.MessagingException
import javax.mail.SendFailedException

class SignupMailActivity : AppCompatActivity() {

    private lateinit var btnAuth: Button
    private lateinit var btnNext: Button
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtAuthNum: EditText
    private lateinit var txtAlert: TextView
    private lateinit var authCode: String
    private val gMailSender = GmailSender("joonho340@gmail.com", "fmjyziwxaplloryx")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_mail)

        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        btnAuth = findViewById(R.id.btn_signup_auth_req)
        btnNext = findViewById(R.id.btn_signup_netx)
        edtName=findViewById(R.id.edt_signup_name)
        edtEmail = findViewById(R.id.edt_signup_email)
        edtAuthNum = findViewById(R.id.edt_signup_auth)
        txtAlert = findViewById(R.id.txt_signup_alert)
    }

    private fun setListeners() {
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isValid = isValidEmail(s.toString())
                updateAuthButtonState(isValid)
            }
        })

        btnAuth.setOnClickListener {
            sendEmail(edtEmail.text.toString())
            updateAuthButtonState(false)
        }

        edtAuthNum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateNextButtonState(!s.isNullOrEmpty())
            }
        })

        btnNext.setOnClickListener {
            if (authCode == edtAuthNum.text.toString()) {
                startActivity(Intent(this, SignupPwActivity::class.java))
                intent.putExtra("email", edtEmail.text.toString())
                intent.putExtra("name",edtName.text.toString())
            } else {
                Toast.makeText(applicationContext, "잘못된 인증번호 입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendEmail(address: String) {
        authCode = createNumberCode()
        Thread {
            try {
                Log.i("인증번호", authCode)
                gMailSender.sendMail("KocoaTalk 인증메일입니다.", "인증번호: $authCode", address)
                showToast("송신 완료")
            } catch (e: SendFailedException) {
                showToast("이메일 형식이 잘못되었습니다.")
            } catch (e: MessagingException) {
                Log.e("메일 전송 오류", "인터넷 문제: $e")
                showToast("인터넷 연결을 확인해 주십시오")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun createNumberCode(): String {
        val str = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        return (1..6).map { str.random() }.joinToString("")
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAuthButtonState(isEnabled: Boolean) {
        btnAuth.isEnabled = isEnabled
        btnAuth.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (isEnabled) R.color.green else R.color.gray
            )
        )
    }

    private fun updateNextButtonState(isEnabled: Boolean) {
        btnNext.isEnabled = isEnabled
        btnNext.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (isEnabled) R.color.green else R.color.gray
            )
        )
    }
}