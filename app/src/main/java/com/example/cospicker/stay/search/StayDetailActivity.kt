package com.example.cospicker.stay.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cospicker.R
import com.example.cospicker.databinding.StayDetailBinding
import com.example.cospicker.stay.model.Stay

class StayDetailActivity : AppCompatActivity() {

    private lateinit var binding: StayDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StayDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getSerializableExtra("stayData") as? Stay ?: return

        binding.txtName.text = data.name
        binding.txtAddress.text = data.address
        binding.txtPrice.text = "${data.price}원"

        Glide.with(this)
            .load(data.imageUrl)
            .placeholder(R.drawable.no_image)
            .into(binding.imgStay)
    }
}
