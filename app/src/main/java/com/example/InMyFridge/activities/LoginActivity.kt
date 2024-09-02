package com.example.kocoatalk.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kocoatalk.R
import com.example.kocoatalk.Utils.*
import com.example.kocoatalk.databinding.ActivityLoginBinding

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    private lateinit var prefUtil: PreferenceUtil
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        setOnClickListeners()
    }

    private fun initializeViews() {
        prefUtil = PreferenceUtil(this)
        pref = prefUtil.getPreferences("pref_logedin_user")

        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.social_login_info_naver_client_id),
            getString(R.string.social_login_info_naver_client_secret),
            getString(R.string.social_login_info_naver_client_name)
        )
    }

    private fun setOnClickListeners() {
        binding.ttvSignup.setOnClickListener {
            startActivity(Intent(this, SignupMailActivity::class.java))
        }

        binding.btnLogin.setOnClickListener { performLogin() }

        binding.btnKakaologin.setOnClickListener {
            // TODO: 카카오 로그인 로직 추가
        }

        binding.btnNaverlogin.setOnClickListener { performNaverLogin() }

        binding.ttvFindidpw.setOnClickListener {
            // TODO: 아이디/비밀번호 찾기 화면으로 이동
        }
    }

    private fun performLogin() {
        val email = binding.edtEmailLogin.text.toString()
        val password = binding.edtPwLogin.text.toString()

        val retrofit = createRetrofitInstance(LoginInterface.Login_Url)
        val loginService = retrofit.create(LoginInterface::class.java)
        val userLogin = User_login(email, password)

        loginService.loginUser(userLogin).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    handleLoginResponse(response.body()?.string(), email)
                } else {
                    showToast("로그인 에러: 가입되지 않은 이메일입니다.")
                    logError("Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showToast("네트워크 오류가 발생했습니다.")
            }
        })
    }

    private fun handleLoginResponse(responseBody: String?, email: String) {
        responseBody?.let {
            val jsonObject = JSONObject(it)
            val message = jsonObject.getString("message")
            val userId = jsonObject.getString("userid")
            val username = jsonObject.getString("username")

            when (message) {
                "Login successful" -> {
                    saveLoginData("email", userId, username)
                    showToast("$username 님 환영합니다.\nuserid: $userId")
                    navigateToMainActivity()
                }
                "Incorrect password" -> showToast("비밀번호가 일치하지 않습니다")
                else -> showToast("가입되지 않은 이메일입니다.")
            }
        }
    }

    private fun performNaverLogin() {
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        result.profile?.let {
                            val name = it.name.toString()
                            val id = it.id.toString()
                            logDebug("Profile: ${it}")
                            registerUser(name, id)
                        }
                    }

                    override fun onError(errorCode: Int, message: String) {
                        logError("NaverLogin Error Code: $errorCode, Message: $message")
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        logError("NaverLogin Failure: HttpStatus: $httpStatus, Message: $message")
                    }
                })
            }

            override fun onError(errorCode: Int, message: String) {
                logError("NaverLogin Error Code: $errorCode, Message: $message")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                logError("NaverLogin Failure: HttpStatus: $httpStatus, Message: $message")
            }
        })
    }

    private fun registerUser(name: String, naverID: String) {
        val retrofit = createRetrofitInstance(SignupInterface.Register_Url)
        val signupService = retrofit.create(SignupInterface::class.java)
        val user = UserSignup(null, null, name, "naver", naverID)

        signupService.registerUser(user).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    handleRegisterResponse(response.body()?.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showToast("네트워크 오류가 발생했습니다.")
            }
        })
    }

    private fun handleRegisterResponse(responseBody: String?) {
        responseBody?.let {
            val jsonObject = JSONObject(it)
            val message = jsonObject.getString("message")
            val userId = jsonObject.getString("userid")
            val username = jsonObject.getString("username")

            when (message) {
                "User registered successfully" -> {
                    showToast("가입이 완료되었습니다.")
                }
                "User already exists" -> {
                    saveLoginData("naver", userId, username)
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun saveLoginData(loginMethod: String, userId: String, username: String) {
        prefUtil.apply {
            setBoolean(pref, "logged in", true)
            setString(pref, "loginmethod", loginMethod)
            setString(pref, "userid", userId)
            setString(pref, "username", username)
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun logError(message: String) {
        Log.e("LoginActivity", message)
    }

    private fun logDebug(message: String) {
        Log.d("LoginActivity", message)
    }

    private fun createRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
