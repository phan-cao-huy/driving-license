package com.example.btlapp

object TrafficSignRepository {
    fun getAllSigns(): List<TrafficSign> {
        return listOf(
            TrafficSign("Đường cấm", "Báo đường cấm tất cả các loại phương tiện đi lại cả hai hướng, trừ các xe được ưu tiên theo quy định.", android.R.drawable.ic_delete, "Cấm"),
            TrafficSign("Cấm đi ngược chiều", "Báo đường cấm tất cả các loại xe đi vào theo chiều đặt biển, trừ các xe được ưu tiên theo quy định.", android.R.drawable.ic_menu_close_clear_cancel, "Cấm"),
            TrafficSign("Công trường", "Báo trước gần tới đoạn đường đang tiến hành tu sửa có người và máy móc đang làm việc.", android.R.drawable.ic_menu_edit, "Cảnh báo"),
            TrafficSign("Hướng đi thẳng phải theo", "Các loại xe chỉ được đi thẳng.", android.R.drawable.arrow_up_float, "Hiệu lệnh")
        )
    }
}
