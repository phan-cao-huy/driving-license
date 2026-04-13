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
import java.util.List;

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

        // Initialize database and load B1 questions from assets
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
        
        // Load default repository data if needed
        if (db.getQuestionsByClass("A1").isEmpty()) {
            for (Question q : QuestionRepository.getMotorbikeQuestions()) {
                db.addQuestion(q, "A1");
            }
        }

        // Load B1 questions from JSON assets
        if (db.getQuestionsByClass("B1").isEmpty()) {
            loadQuestionsFromJson(db, "questions_b1.json", "B1");
        }

        if (db.getAllTrafficSigns().isEmpty()) {
            for (TrafficSign sign : TrafficSignRepository.getAllSigns()) {
                db.addTrafficSign(sign);
            }
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
            
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                
                // Parse options object from your JSON format
                JSONObject optionsObj = obj.getJSONObject("options");
                String optionA = optionsObj.optString("A", "");
                String optionB = optionsObj.optString("B", "");
                String optionC = optionsObj.optString("C", "");
                String optionD = optionsObj.optString("D", null);
                
                // Map correctAnswer "A", "B", "C", "D" to 1, 2, 3, 4
                String ansChar = obj.getString("correctAnswer");
                int ansInt = 1;
                if ("B".equalsIgnoreCase(ansChar)) ansInt = 2;
                else if ("C".equalsIgnoreCase(ansChar)) ansInt = 3;
                else if ("D".equalsIgnoreCase(ansChar)) ansInt = 4;

                Question q = new Question(
                    obj.getInt("id"),
                    obj.getString("content"),
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    ansInt,
                    obj.optString("explanation", ""),
                    null,
                    obj.optBoolean("isCritical", false)
                );
                db.addQuestion(q, licenseClass);
            }
            Log.d("DB", "Loaded questions for " + licenseClass);
        } catch (Exception e) {
            Log.e("DB", "Error loading JSON from " + fileName, e);
        }
    }
}
