package com.example.cospicker.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.auth.LoginIntroActivity
import com.example.cospicker.chat.ChatListActivity
import com.example.cospicker.community.CommunityActivity
import com.example.cospicker.myinfo.ProfileActivity
import com.example.cospicker.stay.search.StaySearchActivity   // ⭐ 추가된 import!
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_main)

        // 🔥 Firebase 로그인 여부 확인
        val isLogin = FirebaseAuth.getInstance().currentUser != null

        // 하단 네비게이션 바
        val navHome = findViewById<LinearLayout>(R.id.nav_home)
        val navCommunity = findViewById<LinearLayout>(R.id.nav_community)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)
        val navMessage = findViewById<LinearLayout>(R.id.nav_message)

        // 숙소 탭
        val stayTab = findViewById<LinearLayout>(R.id.nav_stay)

        // 홈 → 자기 자신이라 동작 없음
        navHome.setOnClickListener { }

        // ⭐ 숙소 → StaySearchActivity 이동
        stayTab.setOnClickListener {
            startActivity(Intent(this, StaySearchActivity::class.java))
        }

        // ⭐ 커뮤니티
        navCommunity.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
        }

        // ⭐ 메시지 → 채팅 리스트 화면
        navMessage.setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        // ⭐ 프로필
        navProfile.setOnClickListener {
            if (isLogin) {
                startActivity(Intent(this, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this, LoginIntroActivity::class.java))
            }
        }
    }
}
