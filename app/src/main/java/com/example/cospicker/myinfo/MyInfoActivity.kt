package com.example.cospicker.myinfo

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyInfoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_main)

        val btnBack = findViewById<ImageView>(R.id.btn_back)

        val rowProfile = findViewById<LinearLayout>(R.id.row_profile)
        val rowName = findViewById<LinearLayout>(R.id.row_name)
        val rowPhone = findViewById<LinearLayout>(R.id.row_phone)
        val rowBirth = findViewById<LinearLayout>(R.id.row_birth)
        val rowGender = findViewById<LinearLayout>(R.id.row_gender)
        val rowResetPw = findViewById<LinearLayout>(R.id.row_reset_pw)

        // 뒤로가기
        btnBack.setOnClickListener { finish() }

        // 닉네임 수정
        rowProfile.setOnClickListener {
            startActivity(Intent(this, EditNicknameActivity::class.java))
        }

        // 이름 수정
        rowName.setOnClickListener {
            startActivity(Intent(this, EditNameActivity::class.java))
        }

        // 휴대폰 번호
        rowPhone.setOnClickListener {
            startActivity(Intent(this, EditPhoneActivity::class.java))
        }

        // 생년월일
        rowBirth.setOnClickListener {
            startActivity(Intent(this, EditBirthActivity::class.java))
        }

        // 성별
        rowGender.setOnClickListener {
            startActivity(Intent(this, EditGenderActivity::class.java))
        }

        // 비밀번호 변경
        rowResetPw.setOnClickListener {
            startActivity(Intent(this, EditPasswordActivity::class.java))
        }
    }

    // ⭐ 화면 돌아올 때 Firestore 최신 데이터 반영
    override fun onResume() {
        super.onResume()

        if (uid.isEmpty()) return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null) {

                    // 🔵 닉네임 (top)
                    findViewById<TextView>(R.id.info_nickname).text =
                        doc.getString("nickname") ?: "닉네임"

                    // 🔵 이름 (회원 정보)
                    findViewById<TextView>(R.id.info_name).text =
                        doc.getString("name") ?: "이름"

                    // 🔵 전화번호
                    findViewById<TextView>(R.id.info_phone).text =
                        doc.getString("phone") ?: "휴대폰 번호"

                    // 🔵 생년월일
                    findViewById<TextView>(R.id.info_birth).text =
                        doc.getString("birth") ?: "생년월일"

                    // 🔵 성별
                    findViewById<TextView>(R.id.info_gender).text =
                        doc.getString("gender") ?: "성별"
                }
            }
    }
}
