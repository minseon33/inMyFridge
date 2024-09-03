package com.example.InMyFridge.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.InMyFridge.R
import com.example.InMyFridge.Utils.SignupInterface
import com.example.InMyFridge.Utils.UserSignup
import com.example.InMyFridge.databinding.ActivitySignupPwBinding
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SignupPwActivity : AppCompatActivity() {

    // 이메일과 이름 변수 선언
    private lateinit var rmail: String
    private lateinit var rname: String
    private lateinit var binding: ActivitySignupPwBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent로 받은 이메일과 이름 값을 초기화
        rmail = intent.getStringExtra("email").orEmpty()
        rname = intent.getStringExtra("name").orEmpty()

        // View Binding 설정
        binding = ActivitySignupPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 디버그 로그 출력
        Log.d("SignupDebug", "Received email: $rmail")
        Log.d("SignupDebug", "Received name: $rname")

        // 리스너 설정
        setListeners()
    }

    private fun setListeners() {
        // 비밀번호 입력에 대한 TextWatcher 설정
        binding.edtSignupPw.addTextChangedListener(createTextWatcher { password ->
            val isValid = isValidPw(password.toString())
            updatePwAlert(isValid)
        })

        // 비밀번호 확인 입력에 대한 TextWatcher 설정
        binding.edtSignupPwcheck.addTextChangedListener(createTextWatcher { passwordCheck ->
            updatePwCheckAlert(passwordCheck.toString() == binding.edtSignupPw.text.toString())
        })

        // 완료 버튼 클릭 리스너 설정
        binding.btnSignupFinish.setOnClickListener {
            registerUser()
        }
    }

    // TextWatcher 생성 함수
    private fun createTextWatcher(afterTextChangedAction: (Editable?) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                afterTextChangedAction(s)
            }
        }
    }

    // 비밀번호 유효성 검사를 위한 Alert 업데이트 함수
    private fun updatePwAlert(isValid: Boolean) {
        binding.ttvSignupPwalert.apply {
            setTextColor(ContextCompat.getColor(this@SignupPwActivity, if (isValid) R.color.green else R.color.red))
            text = if (isValid) "유효한 비밀번호 입니다." else text
        }
    }

    // 비밀번호 확인 Alert 및 완료 버튼 상태 업데이트 함수
    private fun updatePwCheckAlert(isMatching: Boolean) {
        binding.btnSignupFinish.apply {
            isEnabled = isMatching
            setBackgroundColor(ContextCompat.getColor(this@SignupPwActivity, if (isMatching) R.color.green else R.color.gray))
        }
        binding.ttvSignupPwcheckalert.apply {
            setTextColor(ContextCompat.getColor(this@SignupPwActivity, if (isMatching) R.color.green else R.color.red))
            text = if (isMatching) "비밀번호가 일치합니다" else "비밀번호가 일치하지 않습니다"
        }
    }

    // 비밀번호 유효성 검사 함수
    private fun isValidPw(password: String): Boolean {
        val patternSymbol = Pattern.compile("([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])")
        val matcherSymbol = patternSymbol.matcher(password)

        return when {
            password.length < 8 -> {
                showPwError("비밀번호는 8자리 이상이어야 합니다")
                false
            }
            password.contains(" ") -> {
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

    // 비밀번호 유효성 검사 실패 시 오류 메시지 표시 함수
    private fun showPwError(message: String) {
        binding.ttvSignupPwalert.apply {
            setTextColor(ContextCompat.getColor(this@SignupPwActivity, R.color.red))
            text = message
        }
    }

    // 회원 가입 요청 함수
    private fun registerUser() {
        val retrofit = Retrofit.Builder()
            .baseUrl(SignupInterface.Register_Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val signupService = retrofit.create(SignupInterface::class.java)
        val user = UserSignup(rmail, binding.edtSignupPwcheck.text.toString(), rname, "local", null)

        val gson = Gson()
        Log.d("SignupDebug", "User JSON: ${gson.toJson(user)}")

        signupService.registerUser(user).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("SignupDebug", "Response: $response")
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    Log.d("LoginDebug", "Response body: $responseBody")
                    responseBody?.let {
                        val message = JSONObject(it).getString("message")
                        if (message == "User registered successfully") {
                            Toast.makeText(applicationContext, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignupPwActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
