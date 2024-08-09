package com.example.kocoatalk.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kocoatalk.R
import com.example.kocoatalk.Utils.SignupInterface
import com.example.kocoatalk.Utils.User
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SignupPwActivity : AppCompatActivity() {

    // View declarations
    private lateinit var edtPw: EditText
    private lateinit var edtPwCheck: EditText
    private lateinit var ttvPw: TextView
    private lateinit var ttvPwCheck: TextView
    private lateinit var btnFinish: Button
    private lateinit var rmail:String
    private lateinit var rname :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_pw)

        initializeViews()
        setListeners()
    }

    private fun initializeViews() {
        edtPw = findViewById(R.id.edt_signup_pw)
        edtPwCheck = findViewById(R.id.edt_signup_pwcheck)
        ttvPw = findViewById(R.id.ttv_signup_pwalert)
        ttvPwCheck = findViewById(R.id.ttv_signup_pwcheckalert)
        btnFinish = findViewById(R.id.btn_signup_finish)
        val email: String = intent.getStringExtra("email") ?: ""
        val name: String = intent.getStringExtra("name") ?: ""
rmail=email
        rname=name
        Log.d("SignupDebug", "received email: "+email)
        Log.d("SignupDebug", "received name: "+name)
    }

    private fun setListeners() {
        edtPw.addTextChangedListener(createTextWatcher { password ->
            val isValid = isValidPw(password.toString())
            updatePwAlert(isValid)
        })

        edtPwCheck.addTextChangedListener(createTextWatcher { passwordCheck ->
            updatePwCheckAlert(passwordCheck.toString() == edtPw.text.toString())
        })



        btnFinish.setOnClickListener {
            val email = this.rmail
            val password = edtPwCheck.text.toString()
            val name = this.rname

            val retrofit = Retrofit.Builder()
                .baseUrl(SignupInterface.Register_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val signupService = retrofit.create(SignupInterface::class.java)
            val user = User(email, password, name)

            val gson = Gson()
            val userJson = gson.toJson(user)
            Log.d("SignupDebug", "User JSON: $userJson")

            signupService.registerUser(user).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    Log.d("SignupDebug", "Response: "+response.toString())
                    if (response.isSuccessful) {
                        startActivity(Intent(this@SignupPwActivity, MainActivity::class.java))
                        Toast.makeText(applicationContext, "가입이 완료되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(applicationContext, "가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            })


        }
    }

    private fun createTextWatcher(afterTextChangedAction: (Editable?) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                afterTextChangedAction(s)
            }
        }
    }

    private fun updatePwAlert(isValid: Boolean) {
        ttvPw.setTextColor(
            ContextCompat.getColor(
                this,
                if (isValid) R.color.green else R.color.red
            )
        )
        ttvPw.text = if (isValid) "유효한 비밀번호 입니다." else ttvPw.text
    }

    private fun updatePwCheckAlert(isMatching: Boolean) {
        btnFinish.isEnabled = isMatching
        btnFinish.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (isMatching) R.color.green else R.color.gray
            )
        )
        ttvPwCheck.setTextColor(
            ContextCompat.getColor(
                this,
                if (isMatching) R.color.green else R.color.red
            )
        )
        ttvPwCheck.text = if (isMatching) "비밀번호가 일치합니다" else "비밀번호가 일치하지 않습니다"
    }

    private fun isValidPw(password: String): Boolean {
        val valSymbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])"
        val patternSymbol = Pattern.compile(valSymbol)
        val matcherSymbol = patternSymbol.matcher(password)

        val isLengthValid = password.length >= 8
        val hasNoSpaces = !password.contains(" ")

        return when {
            !isLengthValid -> {
                showPwError("비밀번호는 8자리 이상이어야 합니다")
                false
            }

            !hasNoSpaces -> {
                showPwError("비밀번호에 공백이 포함될 수 없습니다")
                false
            }

            !matcherSymbol.find() -> {
                showPwError("비밀번호에 숫자와 특수문자가 포함되어야 합니다")
                false
            }

            else -> true
        }
    }

    private fun showPwError(message: String) {
        ttvPw.setTextColor(ContextCompat.getColor(this, R.color.red))
        ttvPw.text = message
    }
}