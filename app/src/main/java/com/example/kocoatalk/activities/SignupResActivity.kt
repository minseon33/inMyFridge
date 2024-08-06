package com.example.kocoatalk.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kocoatalk.KocaotalkApplication
import com.example.kocoatalk.R
import java.util.regex.Pattern


class SignupResActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_res)


    }

    fun checkValidation( password: String) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        val valSymbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])"
        // 정규표현식 컴파일
        val patternSymbol = Pattern.compile(valSymbol)
        val matcherSymbol = patternSymbol.matcher(password)

        // 비밀번호 길이 체크
        val isLengthValid = password.length >= 8
        // 비밀번호 공백 체크
        val hasNoSpaces = !password.contains(" ")

        if (matcherSymbol.find() && isLengthValid && hasNoSpaces) {
            // 조건을 모두 만족하는 경우
            // 여기서 원하는 작업 수행
        } else {
            when {
                !isLengthValid -> {
                    // 비밀번호가 8자리 미만일 때
                    Toast.makeText(applicationContext, "비밀번호는 8자리 이상이어야 합니다", Toast.LENGTH_SHORT).show()
                }
                !hasNoSpaces -> {
                    // 비밀번호에 공백이 포함된 경우
                    Toast.makeText(applicationContext, "비밀번호에 공백이 포함될 수 없습니다", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 숫자와 특수문자가 포함되지 않은 경우
                    Toast.makeText(applicationContext, "비밀번호에 숫자와 특수문자가 포함되어야 합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}