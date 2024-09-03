package com.example.InMyFridge.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.InMyFridge.R
import com.example.InMyFridge.Utils.GmailSender
import com.example.InMyFridge.databinding.ActivitySignupMailBinding
import javax.mail.MessagingException
import javax.mail.SendFailedException

class SignupMailActivity : AppCompatActivity() {

    private lateinit var authCode: String
    private val gMailSender = GmailSender("joonho340@gmail.com", "fmjyziwxaplloryx")
    private lateinit var binding: ActivitySignupMailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupMailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.edtSignupEmail.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                updateAuthButtonState(isValidEmail(s.toString()))
            }
        })

        binding.btnSignupAuthReq.setOnClickListener {
            sendEmail(binding.edtSignupEmail.text.toString())
        }

        binding.edtSignupAuth.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                updateNextButtonState(!s.isNullOrEmpty())
            }
        })

        binding.btnSignupNetx.setOnClickListener {
            if (authCode == binding.edtSignupAuth.text.toString()) {
                startSignupPwActivity()
            } else {
                showToast("잘못된 인증번호 입니다.")
            }
        }
    }

    private fun isValidEmail(email: String) = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

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

    private fun createNumberCode() = (1..6).map { (0..9).random().toString() }.joinToString("")

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAuthButtonState(isEnabled: Boolean) {
        binding.btnSignupAuthReq.apply {
            this.isEnabled = isEnabled
            setBackgroundColor(ContextCompat.getColor(this@SignupMailActivity, if (isEnabled) R.color.green else R.color.gray))
        }
    }

    private fun updateNextButtonState(isEnabled: Boolean) {
        binding.btnSignupNetx.apply {
            this.isEnabled = isEnabled
            setBackgroundColor(ContextCompat.getColor(this@SignupMailActivity, if (isEnabled) R.color.green else R.color.gray))
        }
    }

    private fun startSignupPwActivity() {
        Intent(this, SignupPwActivity::class.java).apply {
            putExtra("email", binding.edtSignupEmail.text.toString())
            putExtra("name", binding.edtSignupEmail.text.toString())
            startActivity(this)
        }
    }

    private open class SimpleTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }
}
