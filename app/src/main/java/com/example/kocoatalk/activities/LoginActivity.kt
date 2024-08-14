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
import com.example.kocoatalk.databinding.ActivityLoginBinding
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


    private lateinit var pref: SharedPreferences
    private lateinit var prefutil: PreferenceUtil
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        initializeViews()
        setContentView(binding.root)
        setOnclicks()
    }

    private fun initializeViews() {
        val clientId: String = getString(R.string.social_login_info_naver_client_id)
        val clientSecret: String = getString(R.string.social_login_info_naver_client_secret)
        val clientName: String = getString(R.string.social_login_info_naver_client_name)
        prefutil = PreferenceUtil(this)
        pref = prefutil.getPreferences("pref_logedin_user")
        NaverIdLoginSDK.initialize(this, clientId, clientSecret, clientName)
    }

    private fun setOnclicks() {
        binding.ttvSignup.setOnClickListener {
            val i = Intent(this@LoginActivity, SignupMailActivity::class.java)

            startActivity(i)
        }

        // 다른 버튼 클릭 이벤트 추가
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPwLogin.text.toString()


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
                            } else if (message == "Incorrect password") {
                                Toast.makeText(
                                    applicationContext,
                                    "비밀번호가 일치하지 않습니다",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
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

        binding.btnKakaologin.setOnClickListener {
            // 카카오 로그인 로직 추가
        }

        binding.btnNaverlogin.setOnClickListener {
            // 네이버 로그인 로직 추가
            naverLogin()

        }

        binding.ttvFindidpw.setOnClickListener {
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
                        val id = result.profile?.id.toString()

                        Log.d("NaverLogin", "Name: $name")
                        Log.d("NaverLogin", "Email: $email")
                        Log.d("NaverLogin", "Gender: $gender")
                        Log.d("NaverLogin", "id: $id")
                        Log.d("NaverLogin", "Profile: " + result.profile.toString())
                        val pref = prefutil.getPreferences("pref_logedin_user")
                        prefutil.setString(pref, "loginmethod", "naver")
                        prefutil.setString(pref, "id", id)
                        //여기서 DB에 ID정보랑, 이름, 가입방식 저장해야 함

                        //저장하고 나면 메인 액티비티 부터는 DB에서 생성된 우리 앱 내 유저 고유값인 user_id를 활용해야 함.

                        //기존에 ID가 있다 - api에서 받은 네이버 id값 서버에 전달하고 userid값 받아와야 함.

                        //기존에 id가 없다 - 새로 네이버 id값 db에 입력하고 잘 됐음 응답 받은 다음 메인액티비티 진입
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