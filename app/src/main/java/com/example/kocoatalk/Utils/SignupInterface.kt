package com.example.kocoatalk.Utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST



interface SignupInterface {
    @POST("signup.php")
    fun registerUser(@Body user: UserSignup): Call<ResponseBody>

    companion object {
        const val Register_Url = "http://13.210.31.62/"
    }
}

data class UserSignup(
    val email: String? = null,  // 이메일은 네이버 로그인 사용자의 경우 null 가능
    val password: String? = null,  // 비밀번호도 네이버 로그인 사용자의 경우 null 가능
    val name: String,  // 공통 필드
    val provider: String,  // 로그인 제공자: 'local' 또는 'naver'
    val naver_id: String? = null  // 네이버 사용자의 고유 ID, 일반 사용자의 경우 null
)

