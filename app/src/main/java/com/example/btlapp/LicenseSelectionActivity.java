package com.example.btlapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LicenseSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_license_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String type = getIntent().getStringExtra("LICENSE_TYPE");
        
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        LinearLayout layoutMotorbike = findViewById(R.id.layoutMotorbikeLicenses);
        LinearLayout layoutCar = findViewById(R.id.layoutCarLicenses);

        btnBack.setOnClickListener(v -> finish());

        if ("MOTORBIKE".equals(type)) {
            tvToolbarTitle.setText("Bằng lái xe máy");
            layoutMotorbike.setVisibility(View.VISIBLE);
            layoutCar.setVisibility(View.GONE);
            setupMotorbikeListeners(type);
        } else {
            tvToolbarTitle.setText("Bằng lái ô tô");
            layoutMotorbike.setVisibility(View.GONE);
            layoutCar.setVisibility(View.VISIBLE);
            setupCarListeners(type);
        }
    }

    private void setupMotorbikeListeners(String type) {
        findViewById(R.id.btnA1).setOnClickListener(v -> startCategory(type, "A1"));
        findViewById(R.id.btnA2).setOnClickListener(v -> startCategory(type, "A2"));
        findViewById(R.id.btnA3).setOnClickListener(v -> startCategory(type, "A3"));
        findViewById(R.id.btnA4).setOnClickListener(v -> startCategory(type, "A4"));
    }

    private void setupCarListeners(String type) {
        findViewById(R.id.btnB1).setOnClickListener(v -> startCategory(type, "B1"));
        findViewById(R.id.btnB2).setOnClickListener(v -> startCategory(type, "B2"));
        findViewById(R.id.btnC).setOnClickListener(v -> startCategory(type, "C"));
        findViewById(R.id.btnD).setOnClickListener(v -> startCategory(type, "D"));
    }

    private void startCategory(String type, String licenseClass) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("LICENSE_TYPE", type);
        intent.putExtra("LICENSE_CLASS", licenseClass);
        startActivity(intent);
    }
}
