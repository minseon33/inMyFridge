package com.example.kocoatalk.activities



import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kocoatalk.R
import com.example.kocoatalk.Utils.PreferenceUtil


class MainActivity : AppCompatActivity() {

    private lateinit var prefUtil: PreferenceUtil
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefUtil = PreferenceUtil(this)
        pref = prefUtil.getPreferences("pref_logedin_user")
        val method=prefUtil.getString(pref, "loginmethod", "")
        val id=prefUtil.getString(pref, "userid", "")
        val name=prefUtil.getString(pref, "username", "")
        Log.e("MainActivity", "Login Method : $method")
        Log.e("MainActivity", "User ID : $id")
        Log.e("MainActivity", "User Name : $name")


    }
}