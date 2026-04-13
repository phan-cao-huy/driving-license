package com.example.btlapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;

public class TheoryActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentIndex = 0;

    private TextView tvQuestionCount;
    private TextView tvQuestionContent;
    private RadioGroup rgOptions;
    private RadioButton rbOptionA;
    private RadioButton rbOptionB;
    private RadioButton rbOptionC;
    private RadioButton rbOptionD;
    private TextView tvExplanation;
    private Button btnPrevious;
    private Button btnNext;

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

        initViews();
        loadData();
        displayQuestion();

        btnNext.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
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

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                showExplanation();
            }
        });
    }

    private void initViews() {
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        rgOptions = findViewById(R.id.rgOptions);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        tvExplanation = findViewById(R.id.tvExplanation);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
    }

    private void loadData() {
        String type = getIntent().getStringExtra("LICENSE_TYPE");
        if (type == null) type = "MOTORBIKE";
        
        if (type.equals("MOTORBIKE")) {
            questions = QuestionRepository.getMotorbikeQuestions();
        } else {
            questions = QuestionRepository.getCarQuestions();
        }
    }

    private void displayQuestion() {
        Question question = questions.get(currentIndex);
        tvQuestionCount.setText("Câu " + (currentIndex + 1) + "/" + questions.size());
        tvQuestionContent.setText(question.getContent());
        rbOptionA.setText(question.getOptionA());
        rbOptionB.setText(question.getOptionB());
        
        if (question.getOptionC() != null) {
            rbOptionC.setVisibility(View.VISIBLE);
            rbOptionC.setText(question.getOptionC());
        } else {
            rbOptionC.setVisibility(View.GONE);
        }

        if (question.getOptionD() != null) {
            rbOptionD.setVisibility(View.VISIBLE);
            rbOptionD.setText(question.getOptionD());
        } else {
            rbOptionD.setVisibility(View.GONE);
        }

        rgOptions.clearCheck();
        tvExplanation.setVisibility(View.GONE);
        tvExplanation.setText("Giải thích: " + question.getExplanation());
    }

    private void showExplanation() {
        tvExplanation.setVisibility(View.VISIBLE);
    }
}
