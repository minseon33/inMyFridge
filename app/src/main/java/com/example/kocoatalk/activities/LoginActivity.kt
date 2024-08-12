package com.example.kocoatalk.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kocoatalk.R
import com.example.kocoatalk.Utils.LoginInterface
import com.example.kocoatalk.Utils.PreferenceUtil
import com.example.kocoatalk.Utils.User_login
import com.google.gson.Gson
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback

import com.navercorp.nid.oauth.view.NidOAuthLoginButton
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

import com.nhn.android.oauth.databinding.ActivityOauthWebviewBinding
import okhttp3.ResponseBody
import org.json.JSONObject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var txt_signup: TextView
    private lateinit var txt_findidpw: TextView
    private lateinit var edt_email: EditText
    private lateinit var edt_password: EditText
    private lateinit var btn_login: Button
    private lateinit var btn_kakoLogin: Button
    private lateinit var btn_naver: NidOAuthLoginButton
    private lateinit var pref: SharedPreferences
    private lateinit var prefutil: PreferenceUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setOnclicks()
    }

    private fun initializeViews() {
        val clientId: String = getString(R.string.social_login_info_naver_client_id)
        val clientSecret: String = getString(R.string.social_login_info_naver_client_secret)
        val clientName: String = getString(R.string.social_login_info_naver_client_name)
        txt_signup = findViewById(R.id.ttv_signup)
        txt_findidpw = findViewById(R.id.ttv_findidpw)
        btn_login = findViewById(R.id.btn_login)
        btn_kakoLogin = findViewById(R.id.btn_kakaologin)
        btn_naver = findViewById(R.id.btn_naverlogin)
        edt_email = findViewById(R.id.edt_email_login)
        prefutil = PreferenceUtil(this)
        pref = prefutil.getPreferences("pref_logedin_user")
        edt_password = findViewById(R.id.edt_pw_login)
        NaverIdLoginSDK.initialize(this, clientId, clientSecret, clientName)
    }

    private fun setOnclicks() {
        txt_signup.setOnClickListener {
            val i = Intent(this@LoginActivity, SignupMailActivity::class.java)

            startActivity(i)
        }

        // 다른 버튼 클릭 이벤트 추가
        btn_login.setOnClickListener {
            val email = edt_email.text.toString()
            val password = edt_password.text.toString()


            val retrofit = Retrofit.Builder()
                .baseUrl(LoginInterface.Login_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val loginService = retrofit.create(LoginInterface::class.java)
            val user_login = User_login(email, password)

            val gson = Gson()
            val userJson = gson.toJson(user_login)
            Log.d("LoginDebug", "User JSON: $userJson")

            loginService.loginUser(user_login).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful) {//통신 잘 됐는지 확인
                        val responseBody = response.body()?.string()
                        Log.d("LoginDebug", "Response body: $responseBody")
                        responseBody?.let {
                            val jsonObject = JSONObject(it)
                            val message = jsonObject.getString("message") // message 값 추출

                            // message 값이 "Login successful"인지 확인
                            if (message == "Login successful") {
                                prefutil.setString(pref, "loginmethod", "email")
                                prefutil.setString(pref, "email", email)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            else if (message == "Incorrect password") {
                                Toast.makeText(
                                    applicationContext,
                                    "비밀번호가 일치하지 않습니다",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            else  {
                                Toast.makeText(
                                    applicationContext,
                                    "가입되지 않은 이메일입니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                        // responseBody로 JSON 데이터 파싱 후 필요한 작업 수행
                    } else {
                        Log.d("SignupDebug", "Response error: ${response.errorBody()?.string()}")
                        if (response.errorBody().toString() == "No user found with that email") {
                            Toast.makeText(
                                applicationContext,
                                "로그인 에러: 가입되지 않은 이메일입니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            })


        }

        btn_kakoLogin.setOnClickListener {
            // 카카오 로그인 로직 추가
        }

        btn_naver.setOnClickListener {
            // 네이버 로그인 로직 추가
            naverLogin()

        }

        txt_findidpw.setOnClickListener {
            // 아이디/비밀번호 찾기 화면으로 이동
        }
    }

    private fun naverLogin() {
        val oAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 로그인 성공 시
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val name = result.profile?.name.toString()
                        val email = result.profile?.email.toString()
                        val gender = result.profile?.gender.toString()

                        Log.d("NaverLogin", "Name: $name")
                        Log.d("NaverLogin", "Email: $email")
                        Log.d("NaverLogin", "Profile: "+result.profile.toString())
                        val pref = prefutil.getPreferences("pref_logedin_user")
                        prefutil.setString(pref, "loginmethod", "naver")
                        prefutil.setString(pref, "name", name)
                        // MainActivity로 이동
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)

                        startActivity(intent)

                        finish()
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
                val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                Log.e(
                    "NaverLogin",
                    "Error Code: $errorCode, Message: $message, Token: $naverAccessToken"
                )
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.e("NaverLogin", "Failure: HttpStatus: $httpStatus, Message: $message")
            }
        }

        NaverIdLoginSDK.authenticate(this@LoginActivity, oAuthLoginCallback)
    }


}