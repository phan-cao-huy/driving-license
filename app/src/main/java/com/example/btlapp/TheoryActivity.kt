package com.example.btlapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TheoryActivity : AppCompatActivity() {

    private lateinit var questions: List<Question>
    private var currentIndex = 0

    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuestionContent: TextView
    private lateinit var rgOptions: RadioGroup
    private lateinit var rbOptionA: RadioButton
    private lateinit var rbOptionB: RadioButton
    private lateinit var rbOptionC: RadioButton
    private lateinit var rbOptionD: RadioButton
    private lateinit var tvExplanation: TextView
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_theory)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        loadData()
        displayQuestion()

        btnNext.setOnClickListener {
            if (currentIndex < questions.size - 1) {
                currentIndex++
                displayQuestion()
            }
        }

        btnPrevious.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                displayQuestion()
            }
        }

        rgOptions.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                showExplanation()
            }
        }
    }

    private fun initViews() {
        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        tvQuestionContent = findViewById(R.id.tvQuestionContent)
        rgOptions = findViewById(R.id.rgOptions)
        rbOptionA = findViewById(R.id.rbOptionA)
        rbOptionB = findViewById(R.id.rbOptionB)
        rbOptionC = findViewById(R.id.rbOptionC)
        rbOptionD = findViewById(R.id.rbOptionD)
        tvExplanation = findViewById(R.id.tvExplanation)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun loadData() {
        val type = intent.getStringExtra("LICENSE_TYPE") ?: "MOTORBIKE"
        questions = if (type == "MOTORBIKE") {
            QuestionRepository.getMotorbikeQuestions()
        } else {
            QuestionRepository.getCarQuestions()
        }
    }

    private fun displayQuestion() {
        val question = questions[currentIndex]
        tvQuestionCount.text = "Câu ${currentIndex + 1}/${questions.size}"
        tvQuestionContent.text = question.content
        rbOptionA.text = question.optionA
        rbOptionB.text = question.optionB
        
        if (question.optionC != null) {
            rbOptionC.visibility = View.VISIBLE
            rbOptionC.text = question.optionC
        } else {
            rbOptionC.visibility = View.GONE
        }

        if (question.optionD != null) {
            rbOptionD.visibility = View.VISIBLE
            rbOptionD.text = question.optionD
        } else {
            rbOptionD.visibility = View.GONE
        }

        rgOptions.clearCheck()
        tvExplanation.visibility = View.GONE
        tvExplanation.text = "Giải thích: ${question.explanation}"
    }

    private fun showExplanation() {
        tvExplanation.visibility = View.VISIBLE
    }
}
