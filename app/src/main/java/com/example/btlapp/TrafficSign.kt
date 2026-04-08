package com.example.btlapp

data class TrafficSign(
    val name: String,
    val description: String,
    val imageResId: Int,
    val category: String // "Cấm", "Cảnh báo", "Hiệu lệnh", "Chỉ dẫn"
)
