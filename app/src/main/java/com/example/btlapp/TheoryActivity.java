package com.example.btlapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheoryActivity extends AppCompatActivity {

    private List<Question> allQuestions;
    private List<Question> displayedQuestions;
    private int currentIndex = 0;
    private DatabaseHelper dbHelper;
    private Map<Integer, Integer> userAnswers = new HashMap<>(); 
    private String currentFilter = "ALL";
    private String licenseClass;

    private TextView tvQuestionCount;
    private TextView tvQuestionContent;
    private ImageView ivQuestionImage;
    private RadioGroup rgOptions;
    private RadioButton rbOptionA;
    private RadioButton rbOptionB;
    private RadioButton rbOptionC;
    private RadioButton rbOptionD;
    private TextView tvExplanation;
    private View btnPrevious;
    private View btnNext;
    private ImageView btnBack;
    private TextView tvToolbarTitle;
    private TextView tvFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadData();
        displayQuestion();

        btnNext.setOnClickListener(v -> {
            if (displayedQuestions != null && currentIndex < displayedQuestions.size() - 1) {
                currentIndex++;
                displayQuestion();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayQuestion();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        tvFilter.setOnClickListener(v -> showFilterDialog());

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                saveCurrentAnswer(checkedId);
                showResultAndExplanation();
            }
        });
    }

    private void initViews() {
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        ivQuestionImage = findViewById(R.id.ivQuestionImage);
        rgOptions = findViewById(R.id.rgOptions);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        tvExplanation = findViewById(R.id.tvExplanation);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvFilter = findViewById(R.id.tvFilter);
    }

    private void loadData() {
        licenseClass = getIntent().getStringExtra("LICENSE_CLASS");
        if (licenseClass == null) licenseClass = "A1";
        
        tvToolbarTitle.setText("Ôn thi hạng " + licenseClass);
        applyFilter("ALL");
    }

    private void applyFilter(String filterType) {
        currentFilter = filterType;
        displayedQuestions = dbHelper.getFilteredQuestions(licenseClass, filterType);
        currentIndex = 0;
        
        if (displayedQuestions.isEmpty()) {
            Toast.makeText(this, "Không có câu hỏi nào khớp với bộ lọc", Toast.LENGTH_SHORT).show();
        }
        displayQuestion();
    }

    private void showFilterDialog() {
        String[] options = {
            "Tất cả", 
            "Câu hỏi điểm liệt", 
            "Khái niệm và quy tắc", 
            "Câu sai", 
            "Đã làm", 
            "Chưa làm", 
            "Câu có hình ảnh"
        };
        String[] filterKeys = {
            "ALL", 
            "CRITICAL", 
            "CONCEPTS", 
            "WRONG", 
            "DONE", 
            "NOT_DONE", 
            "HAS_IMAGE"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn bộ lọc");
        builder.setItems(options, (dialog, which) -> {
            applyFilter(filterKeys[which]);
            tvFilter.setText(options[which]);
        });
        builder.show();
    }

    private void saveCurrentAnswer(int checkedId) {
        int answer = 0;
        if (checkedId == R.id.rbOptionA) answer = 1;
        else if (checkedId == R.id.rbOptionB) answer = 2;
        else if (checkedId == R.id.rbOptionC) answer = 3;
        else if (checkedId == R.id.rbOptionD) answer = 4;
        
        Question q = displayedQuestions.get(currentIndex);
        userAnswers.put(q.getId(), answer);
        dbHelper.updateUserAnswer(q.getId(), answer);
    }

    private void displayQuestion() {
        if (displayedQuestions == null || displayedQuestions.isEmpty()) {
            tvQuestionContent.setText("Không có dữ liệu câu hỏi cho bộ lọc này.");
            tvQuestionCount.setText("0/0");
            ivQuestionImage.setVisibility(View.GONE);
            rgOptions.setVisibility(View.GONE);
            tvExplanation.setVisibility(View.GONE);
            return;
        }

        rgOptions.setVisibility(View.VISIBLE);
        Question question = displayedQuestions.get(currentIndex);
        tvQuestionCount.setText("Câu " + (currentIndex + 1) + "/" + displayedQuestions.size());
        tvQuestionContent.setText(question.getContent());
        
        // Image logic
        if (question.getImageName() != null && !question.getImageName().isEmpty()) {
            try {
                String folder = "images/carb1/";
                if (licenseClass.startsWith("A")) {
                    folder = "images/motoAA1/";
                }
                InputStream is = getAssets().open(folder + question.getImageName());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivQuestionImage.setImageBitmap(bitmap);
                ivQuestionImage.setVisibility(View.VISIBLE);
                is.close();
            } catch (IOException e) {
                ivQuestionImage.setVisibility(View.GONE);
            }
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }

        // Reset colors
        rbOptionA.setTextColor(Color.BLACK);
        rbOptionB.setTextColor(Color.BLACK);
        rbOptionC.setTextColor(Color.BLACK);
        rbOptionD.setTextColor(Color.BLACK);

        rbOptionA.setText("1. " + question.getOptionA());
        rbOptionB.setText("2. " + question.getOptionB());
        
        if (question.getOptionC() != null && !question.getOptionC().isEmpty()) {
            rbOptionC.setVisibility(View.VISIBLE);
            rbOptionC.setText("3. " + question.getOptionC());
        } else {
            rbOptionC.setVisibility(View.GONE);
        }

        if (question.getOptionD() != null && !question.getOptionD().isEmpty()) {
            rbOptionD.setVisibility(View.VISIBLE);
            rbOptionD.setText("4. " + question.getOptionD());
        } else {
            rbOptionD.setVisibility(View.GONE);
        }

        // Restore answer from DB
        int savedAns = dbHelper.getUserAnswer(question.getId());
        rgOptions.setOnCheckedChangeListener(null);
        rgOptions.clearCheck();
        if (savedAns != 0) {
            if (savedAns == 1) rbOptionA.setChecked(true);
            else if (savedAns == 2) rbOptionB.setChecked(true);
            else if (savedAns == 3) rbOptionC.setChecked(true);
            else if (savedAns == 4) rbOptionD.setChecked(true);
            showResultAndExplanation();
        } else {
            tvExplanation.setVisibility(View.GONE);
        }
        
        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                saveCurrentAnswer(checkedId);
                showResultAndExplanation();
            }
        });
    }

    private void showResultAndExplanation() {
        Question question = displayedQuestions.get(currentIndex);
        int correctAnswer = question.getCorrectAnswer();
        int userAnswer = dbHelper.getUserAnswer(question.getId());

        if (userAnswer == 0) return;

        if (correctAnswer == 1) rbOptionA.setTextColor(Color.parseColor("#2E7D32"));
        else if (correctAnswer == 2) rbOptionB.setTextColor(Color.parseColor("#2E7D32"));
        else if (correctAnswer == 3) rbOptionC.setTextColor(Color.parseColor("#2E7D32"));
        else if (correctAnswer == 4) rbOptionD.setTextColor(Color.parseColor("#2E7D32"));

        if (userAnswer != correctAnswer) {
            if (userAnswer == 1) rbOptionA.setTextColor(Color.RED);
            else if (userAnswer == 2) rbOptionB.setTextColor(Color.RED);
            else if (userAnswer == 3) rbOptionC.setTextColor(Color.RED);
            else if (userAnswer == 4) rbOptionD.setTextColor(Color.RED);
        }

        String explanation = question.getExplanation();
        tvExplanation.setVisibility(View.VISIBLE);
        if (explanation != null && !explanation.trim().isEmpty()) {
            tvExplanation.setText("Giải thích: " + explanation);
        } else {
            tvExplanation.setText("Đáp án đúng là " + correctAnswer);
        }
    }
}
