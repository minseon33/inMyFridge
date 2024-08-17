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
import com.google.gson.Gson
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
        val clientId = getString(R.string.social_login_info_naver_client_id)
        val clientSecret = getString(R.string.social_login_info_naver_client_secret)
        val clientName = getString(R.string.social_login_info_naver_client_name)
        prefUtil = PreferenceUtil(this)
        pref = prefUtil.getPreferences("pref_logedin_user")
        NaverIdLoginSDK.initialize(this, clientId, clientSecret, clientName)
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

        val retrofit = Retrofit.Builder()
            .baseUrl(LoginInterface.Login_Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginService = retrofit.create(LoginInterface::class.java)
        val userLogin = User_login(email, password)

        loginService.loginUser(userLogin).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    handleLoginResponse(response.body()?.string(), email)
                } else {
                    Log.d("LoginDebug", "Response error: ${response.errorBody()?.string()}")
                    Toast.makeText(applicationContext, "로그인 에러: 가입되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleLoginResponse(responseBody: String?, email: String) {
        responseBody?.let {
            val jsonObject = JSONObject(it)
            val message = jsonObject.getString("message")
            val id = JSONObject(it).getString("userid")
            val name = JSONObject(it).getString("username")
            when (message) {
                "Login successful" -> {
                    prefUtil.setBoolean(pref, "logged in", true)
                    prefUtil.setString(pref, "loginmethod", "email")
                    prefUtil.setString(pref, "userid", id)
                    val name = JSONObject(it).getString("username")
                    prefUtil.setString(pref, "username", name)
                    Toast.makeText(applicationContext, "$name 님 환영합니다.\n userid: $id", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                "Incorrect password" -> {
                    Toast.makeText(applicationContext, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(applicationContext, "가입되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun performNaverLogin() {
        val oAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val name = result.profile?.name.toString()
                        val id = result.profile?.id.toString()
                        Log.d("NaverLogin", "Profile: ${result.profile}")

                        registerUser(name, id)
                    }

                    override fun onError(errorCode: Int, message: String) {
                        Log.e("NaverLogin", "Error Code: $errorCode, Message: $message")
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Log.e("NaverLogin", "Failure: HttpStatus: $httpStatus, Message: $message")
                    }
                })
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e("NaverLogin", "Error Code: $errorCode, Message: $message, Token: ${NaverIdLoginSDK.getAccessToken()}")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.e("NaverLogin", "Failure: HttpStatus: $httpStatus, Message: $message")
            }
        }

        NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
    }

    private fun registerUser(navername: String, naverID: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(SignupInterface.Register_Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val signupService = retrofit.create(SignupInterface::class.java)
        val user = UserSignup(null, null, navername, "naver", naverID)

        signupService.registerUser(user).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    handleRegisterResponse(response.body()?.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleRegisterResponse(responseBody: String?) {
        responseBody?.let {
            val message = JSONObject(it).getString("message")
            val id = JSONObject(it).getString("userid")
           val name = JSONObject(it).getString("username")
            when (message) {
                "User registered successfully" -> {
                    Toast.makeText(applicationContext, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
                "User already exists" -> {
                    prefUtil.setBoolean(pref, "logged in", true)
                    prefUtil.setString(pref, "loginmethod", "naver")
                    prefUtil.setString(pref, "userid", id)
                    prefUtil.setString(pref, "username", name)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

        }
    }
}
