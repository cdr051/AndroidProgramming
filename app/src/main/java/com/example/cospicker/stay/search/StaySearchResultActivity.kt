package com.example.cospicker.stay.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cospicker.databinding.StaySearchResultBinding
import com.example.cospicker.stay.model.Stay
import com.example.cospicker.stay.adapter.StayAdapter
import com.google.firebase.firestore.FirebaseFirestore

class StaySearchResultActivity : AppCompatActivity() {

    private lateinit var binding: StaySearchResultBinding
    private lateinit var adapter: StayAdapter
    private val stayList = mutableListOf<Stay>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StaySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyword = intent.getStringExtra("keyword") ?: ""

        adapter = StayAdapter(stayList) { stay ->
            // TODO: 상세 페이지 이동
        }

        binding.recyclerStayResult.layoutManager = LinearLayoutManager(this)
        binding.recyclerStayResult.adapter = adapter

        loadStay(keyword)
    }

    /** 🔥 Firestore에서 숙소 불러오기 */
    private fun loadStay(keyword: String) {

        db.collection("stay")
            .whereGreaterThanOrEqualTo("name", keyword)
            .whereLessThanOrEqualTo("name", keyword + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                stayList.clear()
                for (doc in result) {
                    stayList.add(doc.toObject(Stay::class.java))
                }
                adapter.notifyDataSetChanged()
            }
    }
}
