package com.example.cospicker.stay.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cospicker.databinding.StayListBinding
import com.example.cospicker.stay.adapter.StayAdapter
import com.example.cospicker.stay.model.Stay
import com.google.firebase.firestore.FirebaseFirestore

class StayListActivity : AppCompatActivity() {

    private lateinit var binding: StayListBinding
    private val stayList = mutableListOf<Stay>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyword = intent.getStringExtra("keyword") ?: ""

        // RecyclerView 설정
        binding.recyclerStayList.layoutManager =
            LinearLayoutManager(this)

        val adapter = StayAdapter(stayList) { stay ->
            val intent = Intent(this, StayDetailActivity::class.java)
            intent.putExtra("stayData", stay)
            startActivity(intent)
        }
        binding.recyclerStayList.adapter = adapter

        // Firestore 조회
        db.collection("stay")
            .whereEqualTo("name", keyword)
            .get()
            .addOnSuccessListener { result ->
                stayList.clear()
                for (doc in result) {
                    val item = doc.toObject(Stay::class.java)
                    stayList.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}
