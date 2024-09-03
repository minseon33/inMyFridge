package com.example.InMyFridge.Utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.POST


interface LoginInterface {
    @POST("login.php")
    fun loginUser(@Body user: User_login): Call<ResponseBody>

    companion object {
        const val Login_Url = "http://13.210.31.62/"
    }
}

data class User_login(
    val email: String,
    val password: String,

)

