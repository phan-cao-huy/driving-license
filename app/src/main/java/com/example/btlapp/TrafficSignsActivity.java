package com.example.btlapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrafficSignsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_traffic_signs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rvTrafficSigns = findViewById(R.id.rvTrafficSigns);
        DatabaseHelper db = new DatabaseHelper(this);
        List<TrafficSign> signs = db.getAllTrafficSigns();
        
        if (signs.isEmpty()) {
            signs = TrafficSignRepository.getAllSigns();
        }

        rvTrafficSigns.setAdapter(new TrafficSignAdapter(signs));
    }

    public static class TrafficSignAdapter extends RecyclerView.Adapter<TrafficSignAdapter.ViewHolder> {
        private final List<TrafficSign> signs;

        public TrafficSignAdapter(List<TrafficSign> signs) {
            this.signs = signs;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView ivSign;
            public final TextView tvName;
            public final TextView tvDesc;
            public final TextView tvCategory;

            public ViewHolder(View view) {
                super(view);
                ivSign = view.findViewById(R.id.ivSign);
                tvName = view.findViewById(R.id.tvSignName);
                tvDesc = view.findViewById(R.id.tvSignDescription);
                tvCategory = view.findViewById(R.id.tvSignCategory);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_traffic_sign, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TrafficSign sign = signs.get(position);
            holder.ivSign.setImageResource(sign.getImageResId());
            holder.tvName.setText(sign.getName());
            holder.tvDesc.setText(sign.getDescription());
            holder.tvCategory.setText("Loại: " + sign.getCategory());
        }

        @Override
        public int getItemCount() {
            return signs.size();
        }
    }
}
