package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 📱 휴대폰 번호 수정 화면
 * -------------------------------------------------------------
 * 기능:
 *  - 사용자 휴대폰 번호 입력
 *  - Firestore users/{uid}/phone 갱신
 *  - 변경 완료 팝업 출력
 */
class EditPhoneActivity : AppCompatActivity() {

    // Firebase Firestore & Auth
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_edit_phone)

        /* ---------------------------------------------------------
         * 🔗 View 연결
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnDone = findViewById<Button>(R.id.btn_done)
        val editPhone = findViewById<EditText>(R.id.edit_phone)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기 버튼
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 💾 저장 버튼 → 휴대폰 번호 변경
         * --------------------------------------------------------- */
        btnDone.setOnClickListener {

            val phone = editPhone.text.toString().trim()

            // 입력값 검증
            if (phone.isEmpty()) {
                Toast.makeText(this, "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 번호 형식 검증(간단한 예시)
            if (!phone.matches("^01[0-9]{8,9}$".toRegex())) {
                Toast.makeText(this, "올바른 휴대폰 번호 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firestore 업데이트
            db.collection("users").document(uid)
                .update("phone", phone)
                .addOnSuccessListener {
                    showSuccessDialog("휴대폰 번호가 변경되었습니다.") {
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "변경 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /* ---------------------------------------------------------
     * 🎉 변경 완료 Dialog
     * --------------------------------------------------------- */
    private fun showSuccessDialog(message: String, onClose: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("완료")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> onClose() }
            .setCancelable(false)
            .show()
    }
}
