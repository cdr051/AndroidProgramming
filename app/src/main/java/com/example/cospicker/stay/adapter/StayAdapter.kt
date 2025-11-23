package com.example.cospicker.stay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cospicker.R
import com.example.cospicker.databinding.ItemStayBinding
import com.example.cospicker.stay.model.Stay

class StayAdapter(
    private val items: List<Stay>,
    private val onClick: (Stay) -> Unit
) : RecyclerView.Adapter<StayAdapter.StayViewHolder>() {

    inner class StayViewHolder(val binding: ItemStayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Stay) {
            binding.txtStayName.text = item.name
            binding.txtStayAddress.text = item.address
            binding.txtStayPrice.text = "${item.price}원"

            Glide.with(binding.imgStay.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.no_image)
                .into(binding.imgStay)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StayViewHolder {
        val binding = ItemStayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StayViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
