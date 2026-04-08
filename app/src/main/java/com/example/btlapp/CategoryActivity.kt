package com.example.btlapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val type = intent.getStringExtra("LICENSE_TYPE") ?: "MOTORBIKE"
        val tvTitle = findViewById<TextView>(R.id.tvCategoryTitle)
        tvTitle.text = if (type == "MOTORBIKE") "Ôn thi Xe máy" else "Ôn thi Ô tô"

        findViewById<MaterialCardView>(R.id.cardTheory).setOnClickListener {
            startActivity(Intent(this, TheoryActivity::class.java).apply { putExtra("LICENSE_TYPE", type) })
        }

        findViewById<MaterialCardView>(R.id.cardMockTest).setOnClickListener {
            startActivity(Intent(this, MockTestActivity::class.java).apply { putExtra("LICENSE_TYPE", type) })
        }

        findViewById<MaterialCardView>(R.id.cardSigns).setOnClickListener {
            startActivity(Intent(this, TrafficSignsActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardTips).setOnClickListener {
            startActivity(Intent(this, ExamTipsActivity::class.java))
        }
    }
}
