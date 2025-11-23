package com.example.cospicker.myinfo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cospicker.R
import com.example.cospicker.community.CommunityPostDetailActivity
import com.example.cospicker.community.model.Post
import com.example.cospicker.myinfo.model.NotificationItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationListActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val notiList = mutableListOf<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myinfo_notifications)

        // 뒤로가기
        findViewById<ImageView>(R.id.btn_back).setOnClickListener { finish() }

        recycler = findViewById(R.id.recyclerNotifications)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(notiList) { item ->
            onNotificationClick(item)
        }
        recycler.adapter = adapter

        loadNotifications()
    }

    private fun loadNotifications() {
        val userId = uid ?: return

        db.collection("notifications")
            .document(userId)
            .collection("user_notifications")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                notiList.clear()
                for (doc in snapshot.documents) {
                    val item = doc.toObject(NotificationItem::class.java) ?: continue
                    item.notificationId = doc.id
                    notiList.add(item)
                }

                adapter.notifyDataSetChanged()

                val txtEmpty = findViewById<TextView>(R.id.txtEmpty)
                txtEmpty.visibility = if (notiList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
    }

    private fun onNotificationClick(item: NotificationItem) {
        val userId = uid ?: return

        // 읽음 처리
        db.collection("notifications")
            .document(userId)
            .collection("user_notifications")
            .document(item.notificationId)
            .update("read", true)

        // 해당 게시글 열기
        if (item.postId.isBlank()) {
            Toast.makeText(this, "게시글 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("posts").document(item.postId)
            .get()
            .addOnSuccessListener { doc ->
                val post = doc.toObject(Post::class.java)
                if (post == null) {
                    Toast.makeText(this, "게시글을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                post.postId = doc.id

                val intent = Intent(this, CommunityPostDetailActivity::class.java)
                intent.putExtra("postData", post)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "게시글 불러오기 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
