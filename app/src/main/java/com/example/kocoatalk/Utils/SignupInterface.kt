package com.example.kocoatalk.Utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST


interface SignupInterface {
    @POST("signup.php")
    fun registerUser(@Body user: User_signup): Call<ResponseBody>

    companion object {
        const val Register_Url = "http://13.210.31.62/"
    }
}

data class User_signup(
    val email: String,
    val password: String,
    val name: String
)

