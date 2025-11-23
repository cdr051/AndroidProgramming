package com.example.cospicker.notice

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.notice.model.Notice
import com.example.cospicker.notice.adpater.NoticeAdapter
import com.example.cospicker.R

class NoticeListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_list)

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val noticeListView = findViewById<ListView>(R.id.notice_list)

        btnBack.setOnClickListener { finish() }

        // ⭐ 로컬 임시 공지 데이터
        val noticeList = listOf(
            Notice(
                id = 1,
                title = "서비스 점검 안내",
                date = "2025.01.02",
                content = "1월 5일 오전 2시부터 4시까지 서버 점검이 진행됩니다."
            ),
            Notice(
                id = 2,
                title = "앱 업데이트 안내",
                date = "2025.01.10",
                content = "새로운 기능이 포함된 앱 업데이트가 예정되어 있습니다."
            ),
            Notice(
                id = 3,
                title = "개인정보 처리방침 변경 안내",
                date = "2025.01.15",
                content = "변경되는 개인정보 처리방침을 확인해주세요."
            )
        )

        // 어댑터 연결
        val adapter = NoticeAdapter(this, noticeList)
        noticeListView.adapter = adapter

        // 상세 페이지 이동
        noticeListView.setOnItemClickListener { _, _, position, _ ->
            val item = noticeList[position]

            val intent = Intent(this, NoticeDetailActivity::class.java)
            intent.putExtra("title", item.title)
            intent.putExtra("date", item.date)
            intent.putExtra("content", item.content)
            startActivity(intent)
        }
    }
}
