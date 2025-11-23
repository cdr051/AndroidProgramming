package com.example.cospicker.notice

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R

class NoticeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_detail)

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val title = findViewById<TextView>(R.id.detail_title)
        val date = findViewById<TextView>(R.id.detail_date)
        val content = findViewById<TextView>(R.id.detail_content)

        btnBack.setOnClickListener { finish() }

        title.text = intent.getStringExtra("title")
        date.text = intent.getStringExtra("date")
        content.text = intent.getStringExtra("content")
    }
}
