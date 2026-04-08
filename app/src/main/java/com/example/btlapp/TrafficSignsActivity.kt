package com.example.btlapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class TrafficSignsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_traffic_signs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvTrafficSigns = findViewById<RecyclerView>(R.id.rvTrafficSigns)
        val signs = TrafficSignRepository.getAllSigns()
        rvTrafficSigns.adapter = TrafficSignAdapter(signs)
    }

    class TrafficSignAdapter(private val signs: List<TrafficSign>) :
        RecyclerView.Adapter<TrafficSignAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivSign: ImageView = view.findViewById(R.id.ivSign)
            val tvName: TextView = view.findViewById(R.id.tvSignName)
            val tvDesc: TextView = view.findViewById(R.id.tvSignDescription)
            val tvCategory: TextView = view.findViewById(R.id.tvSignCategory)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_traffic_sign, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sign = signs[position]
            holder.ivSign.setImageResource(sign.imageResId)
            holder.tvName.text = sign.name
            holder.tvDesc.text = sign.description
            holder.tvCategory.text = "Loại: ${sign.category}"
        }

        override fun getItemCount() = signs.size
    }
}
