package com.example.cospicker.stay.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.stay.search.StayListActivity
import com.google.android.flexbox.FlexboxLayout

class StaySearchActivity : AppCompatActivity() {

    private lateinit var recentKeywords: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stay_search)

        val prefs = getSharedPreferences("stay_search", Context.MODE_PRIVATE)

        recentKeywords = prefs.getStringSet("keywords", setOf())?.toMutableList() ?: mutableListOf()

        val container = findViewById<FlexboxLayout>(R.id.container_recent)
        val edtSearch = findViewById<EditText>(R.id.edtSearch)
        val btnClearAll = findViewById<TextView>(R.id.txtClearAll)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        updateTags(container)

        btnClearAll.setOnClickListener {
            recentKeywords.clear()
            updateTags(container)
            saveKeywords()
        }

        // ⭐ 엔터 → 검색
        edtSearch.setOnEditorActionListener { _, _, _ ->
            val input = edtSearch.text.toString().trim()
            if (input.isNotEmpty()) {

                addKeyword(input, container)
                edtSearch.text.clear()

                // ⭐ 검색 결과 페이지로 이동
                val intent = Intent(this, StayListActivity::class.java)
                intent.putExtra("keyword", input)
                startActivity(intent)
            }
            true
        }

        // 날짜·인원 BottomSheet (이미 완성됨)
        findViewById<LinearLayout>(R.id.btnDatePeople).setOnClickListener {
            val sheet = StayDatePeopleBottomSheet { start, end, people ->
                findViewById<TextView>(R.id.txtDate).text =
                    if (start.isNotEmpty() && end.isNotEmpty()) "$start - $end" else start

                findViewById<TextView>(R.id.txtPeople).text = "${people}명"
            }
            sheet.show(supportFragmentManager, "date_people")
        }
    }

    private fun updateTags(container: FlexboxLayout) {
        container.removeAllViews()
        for (keyword in recentKeywords) addTagView(keyword, container)
    }

    private fun addTagView(keyword: String, container: FlexboxLayout) {
        val tv = TextView(this).apply {
            text = "$keyword  ×"
            setBackgroundResource(R.drawable.tag_bg)
            textSize = 14f
            setPadding(35, 20, 35, 20)
            setTextColor(0xFF000000.toInt())

            setOnClickListener {
                findViewById<EditText>(R.id.edtSearch).setText(keyword)
            }

            setOnLongClickListener {
                recentKeywords.remove(keyword)
                updateTags(container)
                saveKeywords()
                true
            }
        }

        val params = FlexboxLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 10, 10, 10)
        container.addView(tv, params)
    }

    private fun addKeyword(keyword: String, container: FlexboxLayout) {
        if (!recentKeywords.contains(keyword)) {
            recentKeywords.add(0, keyword)
            updateTags(container)
            saveKeywords()
        }
    }

    private fun saveKeywords() {
        val prefs = getSharedPreferences("stay_search", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("keywords", recentKeywords.toSet()).apply()
    }
}
