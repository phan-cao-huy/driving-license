package com.example.btlapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MockTestActivity : AppCompatActivity() {

    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private val userAnswers = mutableMapOf<Int, Int>() // Question Index -> Selected Option

    private lateinit var tvTimer: TextView
    private lateinit var tvCount: TextView
    private lateinit var tvContent: TextView
    private lateinit var rgOptions: RadioGroup
    private lateinit var rbA: RadioButton
    private lateinit var rbB: RadioButton
    private lateinit var rbC: RadioButton
    private lateinit var rbD: RadioButton
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mock_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        loadMockQuestions()
        startTimer()
        displayQuestion()

        btnNext.setOnClickListener {
            saveAnswer()
            if (currentIndex < questions.size - 1) {
                currentIndex++
                displayQuestion()
            }
        }

        btnPrev.setOnClickListener {
            saveAnswer()
            if (currentIndex > 0) {
                currentIndex--
                displayQuestion()
            }
        }

        btnSubmit.setOnClickListener {
            saveAnswer()
            showSubmitDialog()
        }
    }

    private fun initViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvCount = findViewById(R.id.tvMockQuestionCount)
        tvContent = findViewById(R.id.tvMockQuestionContent)
        rgOptions = findViewById(R.id.rgMockOptions)
        rbA = findViewById(R.id.rbMockOptionA)
        rbB = findViewById(R.id.rbMockOptionB)
        rbC = findViewById(R.id.rbMockOptionC)
        rbD = findViewById(R.id.rbMockOptionD)
        btnPrev = findViewById(R.id.btnMockPrevious)
        btnNext = findViewById(R.id.btnMockNext)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    private fun loadMockQuestions() {
        val type = intent.getStringExtra("LICENSE_TYPE") ?: "MOTORBIKE"
        val allQuestions = if (type == "MOTORBIKE") {
            QuestionRepository.getMotorbikeQuestions()
        } else {
            QuestionRepository.getCarQuestions()
        }
        // In a real app, shuffle and take 25. Here we take all for demo.
        questions = allQuestions.shuffled().take(20)
    }

    private fun startTimer() {
        object : CountDownTimer(20 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                calculateResult()
            }
        }.start()
    }

    private fun displayQuestion() {
        val q = questions[currentIndex]
        tvCount.text = "Câu ${currentIndex + 1}/${questions.size}"
        tvContent.text = q.content
        rbA.text = q.optionA
        rbB.text = q.optionB
        
        rbC.visibility = if (q.optionC != null) View.VISIBLE else View.GONE
        rbC.text = q.optionC ?: ""
        
        rbD.visibility = if (q.optionD != null) View.VISIBLE else View.GONE
        rbD.text = q.optionD ?: ""

        rgOptions.clearCheck()
        userAnswers[currentIndex]?.let {
            when (it) {
                1 -> rbA.isChecked = true
                2 -> rbB.isChecked = true
                3 -> rbC.isChecked = true
                4 -> rbD.isChecked = true
            }
        }
    }

    private fun saveAnswer() {
        val selectedId = rgOptions.checkedRadioButtonId
        if (selectedId != -1) {
            val answer = when (selectedId) {
                R.id.rbMockOptionA -> 1
                R.id.rbMockOptionB -> 2
                R.id.rbMockOptionC -> 3
                R.id.rbMockOptionD -> 4
                else -> 0
            }
            userAnswers[currentIndex] = answer
        }
    }

    private fun showSubmitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Nộp bài")
            .setMessage("Bạn có chắc chắn muốn nộp bài?")
            .setPositiveButton("Có") { _, _ -> calculateResult() }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun calculateResult() {
        var correctCount = 0
        var failedCritical = false

        questions.forEachIndexed { index, question ->
            val userAnswer = userAnswers[index]
            if (userAnswer == question.correctAnswer) {
                correctCount++
            } else if (question.isCritical) {
                failedCritical = true
            }
        }

        val resultMessage = if (failedCritical) {
            "Bạn đã TRƯỢT do trả lời sai câu hỏi điểm liệt."
        } else if (correctCount >= 18) {
            "Chúc mừng! Bạn đã ĐẠT ($correctCount/${questions.size})."
        } else {
            "Bạn không đạt ($correctCount/${questions.size})."
        }

        AlertDialog.Builder(this)
            .setTitle("Kết quả")
            .setMessage(resultMessage)
            .setPositiveButton("Đóng") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}
