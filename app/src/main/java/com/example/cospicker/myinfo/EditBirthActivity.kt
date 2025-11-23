package com.example.cospicker.myinfo

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.cospicker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 생년월일 수정 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 연 / 월 / 일 선택 Dialog 제공
 *  - Firestore에 birth 값 업데이트
 *  - 정보 수정 완료 Dialog 표시
 */
class EditBirthActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_edit_birth)

        /* ---------------------------------------------------------
         * 🔗 View 초기화
         * --------------------------------------------------------- */
        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnSave = findViewById<Button>(R.id.btn_save)

        val tvYear = findViewById<TextView>(R.id.tv_year)
        val tvMonth = findViewById<TextView>(R.id.tv_month)
        val tvDay = findViewById<TextView>(R.id.tv_day)

        val boxYear = findViewById<LinearLayout>(R.id.box_year)
        val boxMonth = findViewById<LinearLayout>(R.id.box_month)
        val boxDay = findViewById<LinearLayout>(R.id.box_day)

        /* ---------------------------------------------------------
         * 🔙 닫기 버튼
         * --------------------------------------------------------- */
        btnClose.setOnClickListener { finish() }

        /* ---------------------------------------------------------
         * 📅 생년월일 선택 박스 (연/월/일)
         * --------------------------------------------------------- */
        boxYear.setOnClickListener { showYearPicker(tvYear) }
        boxMonth.setOnClickListener { showMonthPicker(tvMonth) }
        boxDay.setOnClickListener { showDayPicker(tvDay) }

        /* ---------------------------------------------------------
         * 💾 저장 버튼 → Firestore 업데이트
         * --------------------------------------------------------- */
        btnSave.setOnClickListener {
            val birth = "${tvYear.text}-${tvMonth.text}-${tvDay.text}"

            db.collection("users").document(uid)
                .update("birth", birth)
                .addOnSuccessListener {
                    showSuccessDialog("생년월일이 변경되었습니다.") {
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "변경 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /* ---------------------------------------------------------
     * 📍 연도 선택 Dialog
     * --------------------------------------------------------- */
    private fun showYearPicker(tv: TextView) {
        val years = (1950..2025).map { "${it}년" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("연도 선택")
            .setItems(years) { _, i ->
                tv.text = years[i]
            }
            .show()
    }

    /* ---------------------------------------------------------
     * 📍 월 선택 Dialog
     * --------------------------------------------------------- */
    private fun showMonthPicker(tv: TextView) {
        val months = (1..12).map { "${it}월" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("월 선택")
            .setItems(months) { _, i ->
                tv.text = months[i]
            }
            .show()
    }

    /* ---------------------------------------------------------
     * 📍 일 선택 Dialog
     * --------------------------------------------------------- */
    private fun showDayPicker(tv: TextView) {
        val days = (1..31).map { "${it}일" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("일 선택")
            .setItems(days) { _, i ->
                tv.text = days[i]
            }
            .show()
    }

    /* ---------------------------------------------------------
     * ✅ 변경 완료 Dialog
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
