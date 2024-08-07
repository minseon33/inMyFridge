package com.example.kocoatalk.Utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST


interface SignupInterface {
    @POST("register")
    fun registerUser(@Body user: User): Call<ResponseBody>

    companion object {
        const val Register_Url = "http://13.210.31.62/signup.php"
    }
}

data class User(
    val email: String,
    val password: String,
    val name: String
)

