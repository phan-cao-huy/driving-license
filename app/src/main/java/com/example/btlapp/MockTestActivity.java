package com.example.btlapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockTestActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentIndex = 0;
    private Map<Integer, Integer> userAnswers = new HashMap<>(); // Question Index -> Selected Option

    private TextView tvTimer;
    private TextView tvCount;
    private TextView tvContent;
    private RadioGroup rgOptions;
    private RadioButton rbA;
    private RadioButton rbB;
    private RadioButton rbC;
    private RadioButton rbD;
    private Button btnPrev;
    private Button btnNext;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mock_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadMockQuestions();
        startTimer();
        displayQuestion();

        btnNext.setOnClickListener(v -> {
            saveAnswer();
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                displayQuestion();
            }
        });

        btnPrev.setOnClickListener(v -> {
            saveAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                displayQuestion();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            saveAnswer();
            showSubmitDialog();
        });
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvCount = findViewById(R.id.tvMockQuestionCount);
        tvContent = findViewById(R.id.tvMockQuestionContent);
        rgOptions = findViewById(R.id.rgMockOptions);
        rbA = findViewById(R.id.rbMockOptionA);
        rbB = findViewById(R.id.rbMockOptionB);
        rbC = findViewById(R.id.rbMockOptionC);
        rbD = findViewById(R.id.rbMockOptionD);
        btnPrev = findViewById(R.id.btnMockPrevious);
        btnNext = findViewById(R.id.btnMockNext);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void loadMockQuestions() {
        String type = getIntent().getStringExtra("LICENSE_TYPE");
        if (type == null) type = "MOTORBIKE";
        
        List<Question> allQuestions;
        if (type.equals("MOTORBIKE")) {
            allQuestions = QuestionRepository.getMotorbikeQuestions();
        } else {
            allQuestions = QuestionRepository.getCarQuestions();
        }
        
        Collections.shuffle(allQuestions);
        questions = allQuestions.subList(0, Math.min(20, allQuestions.size()));
    }

    private void startTimer() {
        new CountDownTimer(20 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                calculateResult();
            }
        }.start();
    }

    private void displayQuestion() {
        Question q = questions.get(currentIndex);
        tvCount.setText("Câu " + (currentIndex + 1) + "/" + questions.size());
        tvContent.setText(q.getContent());
        rbA.setText(q.getOptionA());
        rbB.setText(q.getOptionB());
        
        if (q.getOptionC() != null) {
            rbC.setVisibility(View.VISIBLE);
            rbC.setText(q.getOptionC());
        } else {
            rbC.setVisibility(View.GONE);
        }
        
        if (q.getOptionD() != null) {
            rbD.setVisibility(View.VISIBLE);
            rbD.setText(q.getOptionD());
        } else {
            rbD.setVisibility(View.GONE);
        }

        rgOptions.clearCheck();
        Integer answer = userAnswers.get(currentIndex);
        if (answer != null) {
            if (answer == 1) rbA.setChecked(true);
            else if (answer == 2) rbB.setChecked(true);
            else if (answer == 3) rbC.setChecked(true);
            else if (answer == 4) rbD.setChecked(true);
        }
    }

    private void saveAnswer() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId != -1) {
            int answer = 0;
            if (selectedId == R.id.rbMockOptionA) answer = 1;
            else if (selectedId == R.id.rbMockOptionB) answer = 2;
            else if (selectedId == R.id.rbMockOptionC) answer = 3;
            else if (selectedId == R.id.rbMockOptionD) answer = 4;
            
            userAnswers.put(currentIndex, answer);
        }
    }

    private void showSubmitDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Nộp bài")
            .setMessage("Bạn có chắc chắn muốn nộp bài?")
            .setPositiveButton("Có", (dialog, which) -> calculateResult())
            .setNegativeButton("Không", null)
            .show();
    }

    private void calculateResult() {
        int correctCount = 0;
        boolean failedCritical = false;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Integer userAnswer = userAnswers.get(i);
            if (userAnswer != null && userAnswer == question.getCorrectAnswer()) {
                correctCount++;
            } else if (question.isCritical()) {
                if (userAnswer != null) {
                    failedCritical = true;
                }
            }
        }

        String resultMessage;
        if (failedCritical) {
            resultMessage = "Bạn đã TRƯỢT do trả lời sai câu hỏi điểm liệt.";
        } else if (correctCount >= 18) {
            resultMessage = "Chúc mừng! Bạn đã ĐẠT (" + correctCount + "/" + questions.size() + ").";
        } else {
            resultMessage = "Bạn không đạt (" + correctCount + "/" + questions.size() + ").";
        }

        new AlertDialog.Builder(this)
            .setTitle("Kết quả")
            .setMessage(resultMessage)
            .setPositiveButton("Đóng", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }
}
