package com.example.cospicker.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * 로그인 & 회원가입 선택 화면
 * ----------------------------------------------------
 * 기능:
 *  - 이메일/비밀번호 로그인
 *  - 회원가입 → 프로필 입력 화면으로 이동
 *  - 로그인 상태 SharedPreferences 저장
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_signup)

        auth = FirebaseAuth.getInstance()

        /* ---------------------------------------------------------
         * 🔗 View 연결
         * --------------------------------------------------------- */
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val edtEmail = findViewById<EditText>(R.id.edt_email)
        val edtPw = findViewById<EditText>(R.id.edt_pw)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnSignup = findViewById<Button>(R.id.btn_signup)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기
         * --------------------------------------------------------- */
        btnBack.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 🔐 로그인 처리
         * --------------------------------------------------------- */
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val pw = edtPw.text.toString().trim()

            // 📝 입력 검증
            if (email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Firebase 로그인
            auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener {

                    /* ---------------------------------------------
                     * ⭐ 로그인 상태 저장 (SharedPreferences)
                     * --------------------------------------------- */
                    val prefs = getSharedPreferences("user", MODE_PRIVATE)
                    prefs.edit().putBoolean("isLogin", true).apply()

                    Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()

                    // 홈 화면 이동
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        /* ---------------------------------------------------------
         * 🆕 회원가입 → 프로필 정보 입력 화면 이동
         * --------------------------------------------------------- */
        btnSignup.setOnClickListener {
            startActivity(Intent(this, ProfileRegisterActivity::class.java))
        }
    }
}
