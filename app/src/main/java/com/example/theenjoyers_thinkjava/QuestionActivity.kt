package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.util.concurrent.TimeUnit

class QuestionActivity : AppCompatActivity() {

    // --- Variabel State Kuis ---
    private lateinit var questionList: List<Question>
    private var currentQuestionIndex = 0
    private val userAnswers = HashMap<Int, Int>()

    // --- Variabel Timer ---
    private var countDownTimer: CountDownTimer? = null
    private val quizTimeInMillis: Long = 45 * 60 * 1000 // 45 menit
    private var timeLeftInMillis: Long = 0

    // --- Variabel UI ---
    private lateinit var titleTextView: TextView
    private lateinit var questionNumberTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var progressTextView: TextView // Progress (1/25)
    private lateinit var timerTextView: TextView    // Timer
    private lateinit var optionAButton: MaterialButton
    private lateinit var optionBButton: MaterialButton
    private lateinit var optionCButton: MaterialButton
    private lateinit var optionDButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var prevButton: MaterialButton
    private lateinit var allOptions: List<MaterialButton>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        initViews()

        val quizCategory = intent.getStringExtra(Dashboard.EXTRA_CATEGORY)
        if (quizCategory != null) {
            titleTextView.text = "Soal $quizCategory"
            loadQuestions(quizCategory)

            if (questionList.isEmpty()) {
                Toast.makeText(this, "Soal untuk kategori '$quizCategory' tidak ditemukan.", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            displayQuestion()
            setupListeners()
            startTimer() // Mulai timer setelah semuanya siap
        } else {
            Toast.makeText(this, "Error: Kategori kuis tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        titleTextView = findViewById(R.id.question_title_text)
        questionNumberTextView = findViewById(R.id.question_number_text)
        questionTextView = findViewById(R.id.question_text)
        progressTextView = findViewById(R.id.tv_question_progress)
        timerTextView = findViewById(R.id.tv_timer)
        optionAButton = findViewById(R.id.option_a_button)
        optionBButton = findViewById(R.id.option_b_button)
        optionCButton = findViewById(R.id.option_c_button)
        optionDButton = findViewById(R.id.option_d_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)

        allOptions = listOf(optionAButton, optionBButton, optionCButton, optionDButton)
    }

    private fun loadQuestions(category: String) {
        // Mengambil 25 soal acak
        questionList = QuestionsRepository.getQuestions(category, 25)
    }

    private fun displayQuestion() {
        resetOptionStyles()

        val question = questionList[currentQuestionIndex]
        val selectedAnswerForThisQuestion = userAnswers[question.id]

        // Update teks soal dan progress
        questionNumberTextView.text = "No ${currentQuestionIndex + 1}"
        questionTextView.text = question.questionText
        progressTextView.text = "${currentQuestionIndex + 1} / ${questionList.size}" // Update progress

        allOptions.forEachIndexed { index, button ->
            button.text = question.options[index]
            if (index == selectedAnswerForThisQuestion) {
                highlightSelectedOption(button)
            }
        }
        updateNavigationButtons()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(quizTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished // Simpan sisa waktu setiap detik
                // Format waktu menjadi MM:SS
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minutes)

                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // Waktu habis, selesaikan kuis secara otomatis
                Toast.makeText(this@QuestionActivity, "Waktu Habis!", Toast.LENGTH_SHORT).show()
                finishQuiz()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan timer untuk mencegah memory leak jika activity dihancurkan
        countDownTimer?.cancel()
    }

    private fun setupListeners() {
        allOptions.forEachIndexed { index, button ->
            button.setOnClickListener {
                val currentQuestionId = questionList[currentQuestionIndex].id
                userAnswers[currentQuestionId] = index // Simpan jawaban user
                highlightSelectedOption(button) // Highlight pilihan
            }
        }

        nextButton.setOnClickListener {
            if (currentQuestionIndex < questionList.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            } else {
                finishQuiz()
            }
        }

        prevButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion()
            }
        }
    }

    private fun updateNavigationButtons() {
        prevButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.INVISIBLE
        if (currentQuestionIndex == questionList.size - 1) {
            nextButton.text = "Selesai"
        } else {
            nextButton.text = "Selanjutnya"
        }
    }

    private fun finishQuiz() {
        countDownTimer?.cancel() // Hentikan timer saat kuis selesai
        // Hitung waktu yang digunakan
        val timeTakenInMillis = quizTimeInMillis - timeLeftInMillis
        val score = calculateScore()
        val categoryName = intent.getStringExtra(Dashboard.EXTRA_CATEGORY) ?: "Kuis"

        val intent = Intent(this, ScoreActivity::class.java).apply {
            putExtra(ScoreActivity.EXTRA_SCORE, score)
            putExtra(ScoreActivity.EXTRA_TIME_TAKEN, timeTakenInMillis)
            putExtra(ScoreActivity.EXTRA_TOTAL_QUESTIONS, questionList.size)
            putExtra(ReviewActivity.EXTRA_QUESTIONS, ArrayList(questionList))
            putExtra(ReviewActivity.EXTRA_USER_ANSWERS, userAnswers)
            putExtra(ReviewActivity.EXTRA_CATEGORY_NAME, categoryName)
        }
        startActivity(intent)
        finish()
    }

    private fun calculateScore(): Int {
        var correctAnswers = 0
        for (question in questionList) {
            val userAnswerIndex = userAnswers[question.id]
            if (userAnswerIndex == question.correctAnswerIndex) {
                correctAnswers++
            }
        }
        return correctAnswers
    }

    private fun resetOptionStyles() {
        allOptions.forEach { button ->
            button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.grey_button_background_d9)
            button.setTextColor(Color.BLACK)
            button.typeface = Typeface.DEFAULT
        }
    }

    private fun highlightSelectedOption(selectedButton: MaterialButton) {
        resetOptionStyles()
        selectedButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.selected_button_background)
        selectedButton.setTextColor(Color.BLACK)
    }

}