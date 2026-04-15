package com.example.btlapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database and load questions from assets
        initDatabase();

        Button btnMotorbike = findViewById(R.id.btnMotorbike);
        Button btnCar = findViewById(R.id.btnCar);

        btnMotorbike.setOnClickListener(v -> {
            Intent intent = new Intent(this, LicenseSelectionActivity.class);
            intent.putExtra("LICENSE_TYPE", "MOTORBIKE");
            startActivity(intent);
        });

        btnCar.setOnClickListener(v -> {
            Intent intent = new Intent(this, LicenseSelectionActivity.class);
            intent.putExtra("LICENSE_TYPE", "CAR");
            startActivity(intent);
        });
    }

    private void initDatabase() {
        DatabaseHelper db = new DatabaseHelper(this);
        
        // Load A1 questions from JSON assets
        if (db.getQuestionsByClass("A1").isEmpty()) {
            loadQuestionsFromJson(db, "questions_a1_250.json", "A1");
        }

        // Load B1 questions from JSON assets (Hạng B)
        if (db.getQuestionsByClass("B1").isEmpty()) {
            loadQuestionsFromJson(db, "questions_b1.json", "B1");
        }

        // Load Traffic Signs from JSON assets
        if (db.getAllTrafficSigns().isEmpty()) {
            loadTrafficSignsFromJson(db, "bien_bao.json");
        }
    }

    private void loadQuestionsFromJson(DatabaseHelper db, String fileName, String licenseClass) {
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            
            // Danh sách ID câu hỏi điểm liệt cho Ô tô (60 câu theo luật)
            Set<Integer> criticalIds = new HashSet<>();
            if (licenseClass.equals("B1") || licenseClass.equals("B2")) {
                int[] bCriticals = {17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
                for (int id : bCriticals) criticalIds.add(id);
            }

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = obj.getInt("id");
                
                // Parse options
                JSONObject optionsObj = obj.getJSONObject("options");
                String optionA = optionsObj.optString("A", "");
                String optionB = optionsObj.optString("B", "");
                String optionC = optionsObj.optString("C", "");
                String optionD = optionsObj.optString("D", null);
                
                // Map correctAnswer
                String ansChar = obj.getString("correctAnswer");
                int ansInt = 1;
                if ("B".equalsIgnoreCase(ansChar)) ansInt = 2;
                else if ("C".equalsIgnoreCase(ansChar)) ansInt = 3;
                else if ("D".equalsIgnoreCase(ansChar)) ansInt = 4;

                String imageName = obj.optString("image", null);
                String content = obj.getString("content");

                // Logic xác định câu điểm liệt:
                // 1. Dựa trên danh sách ID quy định
                // 2. Hoặc dựa trên từ khóa trong nội dung
                boolean isCritical = criticalIds.contains(id) || content.contains("CÂU LIỆT");

                Question q = new Question(
                    id,
                    content,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    ansInt,
                    obj.optString("explanation", ""),
                    null,
                    imageName,
                    isCritical
                );
                db.addQuestion(q, licenseClass);
            }
            Log.d("DB", "Loaded questions for " + licenseClass + " from " + fileName);
        } catch (Exception e) {
            Log.e("DB", "Error loading JSON from " + fileName, e);
        }
    }

    private void loadTrafficSignsFromJson(DatabaseHelper db, String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                
                String name = obj.getString("ten_bien");
                String description = obj.getString("thong_tin");
                String imageName = obj.getString("image");
                
                String category = "Biển báo";
                if (name.contains("Cấm")) category = "Biển báo cấm";
                else if (name.contains("nguy hiểm")) category = "Biển báo nguy hiểm";
                else if (name.contains("hiệu lệnh")) category = "Biển báo hiệu lệnh";
                else if (name.contains("chỉ dẫn")) category = "Biển báo chỉ dẫn";

                TrafficSign sign = new TrafficSign(name, description, imageName, category);
                db.addTrafficSign(sign);
            }
        } catch (Exception e) {
            Log.e("DB", "Error loading traffic signs", e);
        }
    }
}
