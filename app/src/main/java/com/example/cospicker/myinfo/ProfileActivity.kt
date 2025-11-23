package com.example.cospicker.myinfo

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cospicker.R
import com.example.cospicker.auth.LoginIntroActivity
import com.example.cospicker.chat.ChatListActivity
import com.example.cospicker.home.HomeActivity
import com.example.cospicker.notice.NoticeListActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_profile)

        // ===========================================
        // 🔥 로그인 체크
        // ===========================================
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginIntroActivity::class.java))
            finish()
            return
        }

        // ===========================================
        // ✅ 상단 4개 메뉴 (최근 본 상품, 내 글, 댓글, 알림)
        // ===========================================
        val menuRecent = findViewById<LinearLayout>(R.id.menu_recent)        // ← XML에 id 추가 필요
        val menuMyPosts = findViewById<LinearLayout>(R.id.menu_my_posts)    // ← “내 글”
        val menuMyComments = findViewById<LinearLayout>(R.id.menu_my_comments) // ← “댓글”
        val menuNotify = findViewById<LinearLayout>(R.id.menu_notify)       // ← “알림”

        // ⭐ 최근 본 상품
        menuRecent?.setOnClickListener {
            //startActivity(Intent(this, RecentViewActivity::class.java))
        }

        // ⭐ 내 글
        menuMyPosts?.setOnClickListener {
            startActivity(Intent(this, MyPostsActivity::class.java))
        }

        // ⭐ 내가 쓴 댓글들
        menuMyComments?.setOnClickListener {
            startActivity(Intent(this, MyCommentsActivity::class.java))
        }

        // ⭐ 알림 목록
        menuNotify?.setOnClickListener {
            startActivity(Intent(this, NotificationListActivity::class.java))
        }

        // ===========================================
        // 🔵 내 정보 관리
        // ===========================================
        val menuMyInfo = findViewById<LinearLayout>(R.id.menu_myinfo)
        menuMyInfo.setOnClickListener {
            startActivity(Intent(this, MyInfoActivity::class.java))
        }

        // ===========================================
        // 🔵 공지사항
        // ===========================================
        val menuNotice = findViewById<LinearLayout>(R.id.menu_notice)
        menuNotice.setOnClickListener {
            startActivity(Intent(this, NoticeListActivity::class.java))
        }

        // ===========================================
        // 🔥 로그아웃 (Firebase)
        // ===========================================
        val menuLogout = findViewById<LinearLayout>(R.id.menu_logout)
        menuLogout.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    auth.signOut()

                    val intent = Intent(this, LoginIntroActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("아니오", null)
                .show()
        }

        // ===========================================
        // 🔵 하단 네비게이션
        // ===========================================
        val navHome = findViewById<LinearLayout>(R.id.nav_home)
        val navProfile = findViewById<LinearLayout>(R.id.nav_profile)
        val navMessage = findViewById<LinearLayout>(R.id.nav_message)

        // 홈
        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // 메세지
        navMessage.setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        // 현재 프로필 화면 → 아무 동작 X
        navProfile.setOnClickListener { }
    }
}
