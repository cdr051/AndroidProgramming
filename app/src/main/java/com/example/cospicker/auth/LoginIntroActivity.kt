package com.example.cospicker.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R

/**
 * 로그인 안내(인트로) 화면
 * - 뒤로가기 버튼
 * - 로그인 화면으로 이동
 */
class LoginIntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_login_intro)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기 버튼
         * - 현재 Activity 종료
         * --------------------------------------------------------- */
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()    // 현재 화면 닫기
        }

        /* ---------------------------------------------------------
         * 🔑 로그인 버튼
         * - 로그인 입력 화면(SignupActivity)으로 이동
         * --------------------------------------------------------- */
        val btnLogin = findViewById<TextView>(R.id.btn_login)
        btnLogin.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
