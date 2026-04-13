package com.example.btlapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.card.MaterialCardView;

public class CategoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String type = getIntent().getStringExtra("LICENSE_TYPE");
        if (type == null) type = "MOTORBIKE";

        TextView tvTitle = findViewById(R.id.tvCategoryTitle);
        tvTitle.setText(type.equals("MOTORBIKE") ? "Ôn thi Xe máy" : "Ôn thi Ô tô");

        final String finalType = type;
        findViewById(R.id.cardTheory).setOnClickListener(v -> {
            Intent intent = new Intent(this, TheoryActivity.class);
            intent.putExtra("LICENSE_TYPE", finalType);
            startActivity(intent);
        });

        findViewById(R.id.cardMockTest).setOnClickListener(v -> {
            Intent intent = new Intent(this, MockTestActivity.class);
            intent.putExtra("LICENSE_TYPE", finalType);
            startActivity(intent);
        });

        findViewById(R.id.cardSigns).setOnClickListener(v -> {
            startActivity(new Intent(this, TrafficSignsActivity.class));
        });

        findViewById(R.id.cardTips).setOnClickListener(v -> {
            startActivity(new Intent(this, ExamTipsActivity.class));
        });
    }
}
