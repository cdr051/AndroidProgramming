package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth

/**
 * 비밀번호 변경 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 현재 비밀번호 입력 (UI만 존재, 실제 검증 없음 → Firebase는 재인증 필요)
 *  - 새 비밀번호 입력 + 확인
 *  - Firebase Auth 비밀번호 변경
 *  - 완료 팝업 표시
 *
 * ⚠ 주의 :
 * Firebase Auth는 보안 때문에 "updatePassword()" 사용 시
 * 최근 로그인한 사용자만 비밀번호 변경이 가능함.
 * 재인증(Re-authenticate) 절차가 필요할 수 있음.
 */
class EditPasswordActivity : AppCompatActivity() {

    // Firebase Auth 인스턴스
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_edit_password)

        /* ---------------------------------------------------------
         * 🔗 View 연결
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnSave = findViewById<Button>(R.id.btn_save_pw)

        val nowPw = findViewById<EditText>(R.id.edit_now_pw)         // 현재 비밀번호
        val newPw = findViewById<EditText>(R.id.edit_new_pw)         // 새 비밀번호
        val newPwCheck = findViewById<EditText>(R.id.edit_new_pw_check) // 새 비밀번호 확인

        /* ---------------------------------------------------------
         * 🔙 닫기 버튼
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 💾 저장 버튼 → 비밀번호 변경 실행
         * --------------------------------------------------------- */
        btnSave.setOnClickListener {

            val inputNowPw = nowPw.text.toString().trim()
            val inputNewPw = newPw.text.toString().trim()
            val inputCheckPw = newPwCheck.text.toString().trim()

            // 새 비밀번호와 확인값 비교
            if (inputNewPw != inputCheckPw) {
                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 길이 검증 예시 (선택)
            if (inputNewPw.length < 6) {
                Toast.makeText(this, "비밀번호는 최소 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /* ---------------------------------------------------------
             * ⚠ Firebase는 비밀번호 변경 시 재인증이 필요할 수 있음
             * 현재 코드는 “최근 로그인 상태”일 때만 동작
             * --------------------------------------------------------- */
            auth.currentUser?.updatePassword(inputNewPw)
                ?.addOnSuccessListener {
                    showSuccessDialog("비밀번호가 변경되었습니다.") {
                        finish()
                    }
                }
                ?.addOnFailureListener {
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
