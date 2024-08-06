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
import com.example.kocoatalk.GmailSender
import com.example.kocoatalk.LoadingDialog
import com.example.kocoatalk.R
import javax.mail.MessagingException
import javax.mail.SendFailedException

class SignupMailActivity : AppCompatActivity() {

    private lateinit var btn_auth: Button
    private lateinit var btn_next: Button
    private lateinit var edt_email: EditText
    private lateinit var edt_authnum: EditText
    private lateinit var txt_alert: TextView
    private lateinit var authcode: String
    var gMailSender = GmailSender("joonho340@gmail.com", "fmjyziwxaplloryx")
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
            btn_auth.isEnabled = false
            btn_auth.setBackgroundColor(
                ContextCompat.getColor(
                    this@SignupMailActivity,
                    R.color.gray
                )
            )
            //전송 직후에 일단 disable시킴
        }

        edt_authnum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    btn_next.isEnabled = false
                    btn_next.setBackgroundColor(
                        ContextCompat.getColor(
                            this@SignupMailActivity,
                            R.color.gray
                        )
                    )


                } else {
                    btn_next.isEnabled = true
                    btn_next.setBackgroundColor(
                        ContextCompat.getColor(
                            this@SignupMailActivity,
                            R.color.green
                        )
                    )
                }
            }
        })

        btn_next.setOnClickListener {

            if (authcode == edt_authnum.text.toString()) {
                val i = Intent(this@SignupMailActivity, SignupResActivity::class.java)
                startActivity(i)
            } else {
                Toast.makeText(applicationContext, "잘못된 인증번호 입니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendEmail(address: String) {
        authcode = createNumberCode()

        Thread {
            try {

                // GMailSender.sendMail(제목, 본문내용, 받는사람)
                Log.i("인증번호", authcode)
                gMailSender.sendMail("KocoaTalk 인증메일입니다. ", "인증번호: $authcode", address)


                // 온클릭과 함께 만들어진 인증번호 이메일에 넣어서 보내기
                runOnUiThread {


                    Toast.makeText(applicationContext, "송신 완료", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SendFailedException) {
                // 쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                runOnUiThread {
                    Toast.makeText(applicationContext, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: MessagingException) {
                println("인터넷 문제: $e")
                // 쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                runOnUiThread {
                    Toast.makeText(applicationContext, "인터넷 연결을 확인해 주십시오", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

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