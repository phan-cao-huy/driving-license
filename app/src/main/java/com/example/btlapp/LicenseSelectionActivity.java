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
    }

    private void setupCarListeners(String type) {
        // Thay thế B1, B2 bằng một nút Hạng B duy nhất
        findViewById(R.id.btnB).setOnClickListener(v -> startCategory(type, "B1"));
    }

    private void startCategory(String type, String licenseClass) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("LICENSE_TYPE", type);
        intent.putExtra("LICENSE_CLASS", licenseClass);
        startActivity(intent);
    }
}
