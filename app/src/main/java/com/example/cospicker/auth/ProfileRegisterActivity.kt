package com.example.cospicker.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 회원가입 - 프로필 입력 화면
 * ----------------------------------------------------
 * 기능:
 *  - 프로필 이미지 선택
 *  - 이름/이메일/비밀번호 입력
 *  - Firebase Auth로 계정 생성
 *  - Firestore에 사용자 정보 저장
 */
class ProfileRegisterActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    // 갤러리 이미지 선택 코드
    private val PICK_IMAGE = 2001
    private var profileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_profile_register)

        auth = FirebaseAuth.getInstance()

        /* ---------------------------------------------------------
         * 🔗 View 매핑
         * --------------------------------------------------------- */
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val imgProfile = findViewById<ImageView>(R.id.profile_image)
        val btnEditImage = findViewById<ImageView>(R.id.btn_edit_image)

        val inputName = findViewById<EditText>(R.id.input_name)
        val inputEmail = findViewById<EditText>(R.id.input_email)
        val inputPw = findViewById<EditText>(R.id.input_pw)
        val inputPwCheck = findViewById<EditText>(R.id.input_pw_check)

        val btnRegister = findViewById<Button>(R.id.btn_register)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기
         * --------------------------------------------------------- */
        btnBack.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 📸 프로필 이미지 선택 (갤러리 열기)
         * --------------------------------------------------------- */
        btnEditImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE)
        }

        /* ---------------------------------------------------------
         * ⭐ 회원가입 버튼 클릭
         * --------------------------------------------------------- */
        btnRegister.setOnClickListener {

            val name = inputName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val pw = inputPw.text.toString().trim()
            val pwCheck = inputPwCheck.text.toString().trim()

            /* -------------------------
             * 📝 입력값 검증
             * ------------------------- */
            if (name.isEmpty() || email.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pw != pwCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pw.length < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* ---------------------------------------------------------
             * 🔥 Firebase Auth 회원가입
             * --------------------------------------------------------- */
            auth.createUserWithEmailAndPassword(email, pw)
                .addOnSuccessListener { authResult ->

                    val uid = authResult.user?.uid
                    if (uid == null) {
                        Toast.makeText(this, "UID 오류 발생", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    /* ---------------------------------------------
                     * 📦 Firestore에 저장할 사용자 정보 구성
                     * --------------------------------------------- */
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "profileImage" to (profileUri?.toString() ?: "")
                    )

                    /* -------------------------------------------------
                     * 🗂️ Firestore 저장
                     * ------------------------------------------------- */
                    db.collection("users").document(uid).set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()

                            // 회원가입 완료 화면으로 이동
                            startActivity(Intent(this, SignupCompleteActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "회원 정보 저장 실패", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "회원가입 실패: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    /* ---------------------------------------------------------
     * 📸 갤러리에서 선택한 이미지 적용
     * --------------------------------------------------------- */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            profileUri = data?.data
            val imgProfile = findViewById<ImageView>(R.id.profile_image)
            imgProfile.setImageURI(profileUri)
        }
    }
}
