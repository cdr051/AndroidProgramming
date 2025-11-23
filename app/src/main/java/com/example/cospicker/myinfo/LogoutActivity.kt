package com.example.cospicker.myinfo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.auth.LoginIntroActivity
import com.google.firebase.auth.FirebaseAuth

class LogoutActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_logout)

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val btnNo = findViewById<Button>(R.id.btn_no)
        val btnYes = findViewById<Button>(R.id.btn_yes)

        // 🔙 뒤로가기
        btnBack.setOnClickListener {
            finish()
        }

        // ❌ 아니오 → 그냥 닫기
        btnNo.setOnClickListener {
            finish()
        }

        // ✔ 예 → 로그아웃 실행
        btnYes.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        // Firebase 로그아웃
        auth.signOut()

        // SharedPreferences 로그인 상태 false
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit().putBoolean("isLogin", false).apply()

        // 로그인 화면으로 이동
        val intent = Intent(this, LoginIntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
