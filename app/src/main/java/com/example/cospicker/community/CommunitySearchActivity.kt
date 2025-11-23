package com.example.cospicker.community

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * 커뮤니티 검색 화면
 * ------------------------------------------------------------
 * 기능:
 *  - 검색어 입력 및 검색 실행
 *  - 글 유형 선택 (스피너)
 *  - 태그 선택 (스피너)
 *  - 최근 검색어 Chip
 *  - 검색 결과 화면으로 이동
 */
class CommunitySearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_search)

        /* ---------------------------------------------------------
         * 🔙 뒤로가기
         * --------------------------------------------------------- */
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // 검색창 요소
        val editSearch = findViewById<EditText>(R.id.editSearch)
        val btnSearchIcon = findViewById<ImageView>(R.id.btnSearchIcon)

        /* ---------------------------------------------------------
         * 🔎 검색 실행 (키보드 엔터)
         * --------------------------------------------------------- */
        editSearch.setOnEditorActionListener { _, _, _ ->
            val keyword = editSearch.text.toString().trim()
            if (keyword.isNotEmpty()) moveToResult(keyword)
            true
        }

        /* ---------------------------------------------------------
         * 🔎 검색 실행 (검색 아이콘 클릭)
         * --------------------------------------------------------- */
        btnSearchIcon.setOnClickListener {
            val keyword = editSearch.text.toString().trim()
            if (keyword.isNotEmpty()) moveToResult(keyword)
        }

        /* =========================================================
         * 🗂 글 유형 스피너
         * (전체 / 일반글 / 플래너 / 숙소 / 맛집 / 후기)
         * ========================================================= */
        val spinnerType = findViewById<Spinner>(R.id.spinnerPostType)
        val txtTypeLabel = findViewById<TextView>(R.id.txtTypeLabel)
        val layoutType = findViewById<LinearLayout>(R.id.layoutTypeSelect)

        val postTypes = listOf("전체", "일반글", "플래너", "숙소", "맛집", "후기")

        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            postTypes
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // 스피너 감싸는 레이아웃 클릭 시 스피너 열기
        layoutType.setOnClickListener { spinnerType.performClick() }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?,
                position: Int, id: Long
            ) {
                txtTypeLabel.text = postTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        /* =========================================================
         * 🏷 태그 스피너
         * (전체 / 맛집 / 숙소 / 카페 / 여행 / 후기 / 자유)
         * ========================================================= */
        val spinnerTag = findViewById<Spinner>(R.id.spinnerTag)
        val txtTagLabel = findViewById<TextView>(R.id.txtTagLabel)
        val layoutTag = findViewById<LinearLayout>(R.id.layoutTagSelect)
        val iconTag = findViewById<ImageView>(R.id.iconTag)

        val tags = listOf("전체", "맛집", "숙소", "카페", "여행", "후기", "자유")

        spinnerTag.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tags
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        layoutTag.setOnClickListener { spinnerTag.performClick() }
        iconTag.setOnClickListener { spinnerTag.performClick() }

        spinnerTag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?,
                position: Int, id: Long
            ) {
                txtTagLabel.text = tags[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        /* ---------------------------------------------------------
         * 🕒 최근 검색어 ChipGroup
         * --------------------------------------------------------- */
        val chipRecent = findViewById<ChipGroup>(R.id.chipRecent)
        val txtClearAll = findViewById<TextView>(R.id.txtClearAll)

        // 🔹 예시 데이터 (추후 SharedPreferences나 Firestore로 연동 가능)
        val dummyRecent = listOf("한성대 맛집", "데이트 코스", "플래너 추천")

        dummyRecent.forEach { text ->
            val chip = Chip(this).apply {
                this.text = text
                isClickable = true
                isCheckable = false
                setOnClickListener { editSearch.setText(text) }
            }
            chipRecent.addView(chip)
        }

        // 최근 검색 전체 삭제
        txtClearAll.setOnClickListener {
            chipRecent.removeAllViews()
        }
    }

    /* ---------------------------------------------------------
     * 🔍 검색 결과 화면 이동
     * --------------------------------------------------------- */
    private fun moveToResult(keyword: String) {
        val intent = Intent(this, CommunitySearchResultActivity::class.java)
        intent.putExtra("keyword", keyword)
        startActivity(intent)
    }
}
