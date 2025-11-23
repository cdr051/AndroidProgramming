package com.example.cospicker.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.home.HomeActivity
import com.example.cospicker.R

/**
 * 회원가입 완료 화면
 * ----------------------------------------------------
 * 기능:
 *  - 닫기 버튼 → 홈으로 이동
 *  - 홈으로 가기 버튼 → 홈으로 이동
 *  - 이전 스택 제거 후 홈 화면으로 복귀
 */
class SignupCompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_signup_complete)

        /* ---------------------------------------------------------
         * 🔗 View 연결
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnGoHome = findViewById<Button>(R.id.btn_go_home)

        /* ---------------------------------------------------------
         * ❌ 닫기 버튼 → 홈으로 이동
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { goHome() }

        /* ---------------------------------------------------------
         * 🏠 '홈으로 가기' 버튼 → 홈으로 이동
         * --------------------------------------------------------- */
        btnGoHome.setOnClickListener { goHome() }
    }

    /**
     * 홈 화면으로 이동
     * - 기존 Activity 스택 모두 제거
     * - 완전히 새로운 홈 화면으로 이동
     */
    private fun goHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
