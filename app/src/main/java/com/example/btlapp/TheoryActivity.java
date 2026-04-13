package com.example.btlapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheoryActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentIndex = 0;
    private DatabaseHelper dbHelper;
    private Map<Integer, Integer> userAnswers = new HashMap<>(); 

    private TextView tvQuestionCount;
    private TextView tvQuestionContent;
    private ImageView ivQuestionImage;
    private RadioGroup rgOptions;
    private RadioButton rbOptionA;
    private RadioButton rbOptionB;
    private RadioButton rbOptionC;
    private RadioButton rbOptionD;
    private TextView tvExplanation;
    private View btnPrevious; // Changed to View because it's a LinearLayout in new layout
    private View btnNext;     // Changed to View because it's a LinearLayout in new layout
    private ImageView btnBack;
    private TextView tvToolbarTitle;

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
            if (questions != null && currentIndex < questions.size() - 1) {
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
    }

    private void loadData() {
        String licenseClass = getIntent().getStringExtra("LICENSE_CLASS");
        if (licenseClass == null) licenseClass = "A1";
        
        tvToolbarTitle.setText("Ôn thi hạng " + licenseClass);
        questions = dbHelper.getQuestionsByClass(licenseClass);
    }

    private void saveCurrentAnswer(int checkedId) {
        int answer = 0;
        if (checkedId == R.id.rbOptionA) answer = 1;
        else if (checkedId == R.id.rbOptionB) answer = 2;
        else if (checkedId == R.id.rbOptionC) answer = 3;
        else if (checkedId == R.id.rbOptionD) answer = 4;
        
        userAnswers.put(currentIndex, answer);
    }

    private void displayQuestion() {
        if (questions == null || questions.isEmpty()) {
            tvQuestionContent.setText("Không có dữ liệu câu hỏi.");
            return;
        }

        Question question = questions.get(currentIndex);
        tvQuestionCount.setText("Câu " + (currentIndex + 1) + "/" + questions.size());
        tvQuestionContent.setText(question.getContent());
        
        // Handle Image
        if (question.getImageResId() != null && question.getImageResId() != 0) {
            ivQuestionImage.setVisibility(View.VISIBLE);
            ivQuestionImage.setImageResource(question.getImageResId());
        } else {
            ivQuestionImage.setVisibility(View.GONE);
        }

        // Reset text color
        rbOptionA.setTextColor(Color.BLACK);
        rbOptionB.setTextColor(Color.BLACK);
        rbOptionC.setTextColor(Color.BLACK);
        rbOptionD.setTextColor(Color.BLACK);

        // Display answers
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

        // Restore previous answer
        rgOptions.setOnCheckedChangeListener(null);
        rgOptions.clearCheck();
        Integer savedAnswer = userAnswers.get(currentIndex);
        if (savedAnswer != null) {
            if (savedAnswer == 1) rbOptionA.setChecked(true);
            else if (savedAnswer == 2) rbOptionB.setChecked(true);
            else if (savedAnswer == 3) rbOptionC.setChecked(true);
            else if (savedAnswer == 4) rbOptionD.setChecked(true);
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
        Question question = questions.get(currentIndex);
        int correctAnswer = question.getCorrectAnswer();
        Integer userAnswer = userAnswers.get(currentIndex);

        if (userAnswer == null) return;

        // Highlight correct answer in Green
        if (correctAnswer == 1) rbOptionA.setTextColor(Color.parseColor("#2E7D32"));
        else if (correctAnswer == 2) rbOptionB.setTextColor(Color.parseColor("#2E7D32"));
        else if (correctAnswer == 3) rbOptionC.setTextColor(Color.parseColor("#2E7D32"));
        else if (correctAnswer == 4) rbOptionD.setTextColor(Color.parseColor("#2E7D32"));

        // Highlight wrong user answer in Red
        if (userAnswer != correctAnswer) {
            if (userAnswer == 1) rbOptionA.setTextColor(Color.RED);
            else if (userAnswer == 2) rbOptionB.setTextColor(Color.RED);
            else if (userAnswer == 3) rbOptionC.setTextColor(Color.RED);
            else if (userAnswer == 4) rbOptionD.setTextColor(Color.RED);
        }

        // Show Explanation
        String explanation = question.getExplanation();
        tvExplanation.setVisibility(View.VISIBLE);
        if (explanation != null && !explanation.trim().isEmpty()) {
            tvExplanation.setText("Giải thích: " + explanation);
        } else {
            tvExplanation.setText("Đáp án đúng là " + correctAnswer);
        }
    }
}
