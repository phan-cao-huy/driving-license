package com.example.btlapp

object QuestionRepository {
    fun getMotorbikeQuestions(): List<Question> {
        return listOf(
            Question(
                1,
                "Phần của đường bộ được sử dụng cho các phương tiện giao thông qua lại là gì?",
                "1. Lề đường.",
                "2. Phần đường xe chạy.",
                "3. Vai đường.",
                null,
                2,
                "Phần đường xe chạy là phần của đường bộ được sử dụng cho các phương tiện giao thông qua lại."
            ),
            Question(
                2,
                "“Làn đường” là gì?",
                "1. Là một phần của phần đường xe chạy được chia theo chiều dọc của đường, sử dụng cho xe chạy.",
                "2. Là một phần của phần đường xe chạy được chia theo chiều dọc của đường, có bề rộng đủ cho xe chạy an toàn.",
                "3. Là đường cho xe ô tô chạy, dừng, đỗ an toàn.",
                null,
                2,
                "Làn đường là một phần của phần đường xe chạy được chia theo chiều dọc của đường, có bề rộng đủ cho xe chạy an toàn."
            ),
            Question(
                3,
                "Người lái xe không được quay đầu xe tại các vị trí nào dưới đây?",
                "1. Ở phần đường dành cho người đi bộ qua đường.",
                "2. Trên cầu, đầu cầu, gầm cầu vượt, ngầm, trong hầm đường bộ, đường cao tốc.",
                "3. Tại nơi đường bộ giao nhau cùng mức với đường sắt.",
                "4. Tất cả các ý nêu trên.",
                4,
                "Đây là câu hỏi điểm liệt. Tuyệt đối không được quay đầu xe tại các vị trí nguy hiểm.",
                null,
                true
            )
        )
    }

    fun getCarQuestions(): List<Question> {
        return getMotorbikeQuestions() + listOf(
            Question(
                4,
                "Khi lái xe ô tô dưới trời mưa sát mặt đường có hiện tượng gì?",
                "1. Tầm nhìn bị hạn chế.",
                "2. Bánh xe dễ bị trượt.",
                "3. Cả ý 1 và ý 2.",
                null,
                3,
                "Trời mưa làm giảm tầm nhìn và mặt đường trơn trượt."
            )
        )
    }
}
