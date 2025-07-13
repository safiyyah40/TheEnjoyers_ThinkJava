package com.example.theenjoyers_thinkjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class ReviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUESTIONS = "extra_questions"
        const val EXTRA_USER_ANSWERS = "extra_user_answers"
        const val EXTRA_CATEGORY_NAME = "extra_category_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Ambil data dari Intent
        val questions = intent.getSerializableExtra(EXTRA_QUESTIONS) as? ArrayList<Question>
        val userAnswers = intent.getSerializableExtra(EXTRA_USER_ANSWERS) as? HashMap<Int, Int>
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME)

        // Cek apakah data valid
        if (questions == null || userAnswers == null || categoryName == null) {
            Toast.makeText(this, "Gagal memuat ulasan.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup UI
        val titleTextView: TextView = findViewById(R.id.tv_review_title)
        val reviewContainer: LinearLayout = findViewById(R.id.ll_review_container)
        val backButton: Button = findViewById(R.id.btn_back_from_review)

        titleTextView.text = "Ulasan Soal $categoryName"
        backButton.setOnClickListener { finish() }

        // Inflate dan populate setiap item soal
        populateReview(questions, userAnswers, reviewContainer)
    }

    private fun populateReview(
        questions: List<Question>,
        userAnswers: HashMap<Int, Int>,
        container: LinearLayout
    ) {
        val inflater = LayoutInflater.from(this)

        for ((index, question) in questions.withIndex()) {
            // Buat view baru dari layout template
            val questionView = inflater.inflate(R.layout.item_review_question, container, false)

            // Dapatkan referensi view di dalam template
            val questionNumber = questionView.findViewById<TextView>(R.id.tv_review_question_number)
            val questionText = questionView.findViewById<TextView>(R.id.tv_review_question_text)
            val optionsContainer = questionView.findViewById<LinearLayout>(R.id.ll_review_options_container)

            // Isi data soal
            questionNumber.text = "No ${index + 1}"
            questionText.text = question.questionText

            // Buat dan warnai tombol pilihan jawaban
            val userAnswer = userAnswers[question.id]

            question.options.forEachIndexed { optionIndex, optionText ->
                // Buat tombol baru untuk setiap pilihan
                val optionButton = inflater.inflate(R.layout.item_review_option_button, optionsContainer, false) as MaterialButton
                optionButton.text = optionText

                // Logika pewarnaan
                when {
                    // Jawaban yang benar
                    optionIndex == question.correctAnswerIndex -> {
                        optionButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green_correct)
                        optionButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                    // Jawaban user yang salah
                    optionIndex == userAnswer && userAnswer != question.correctAnswerIndex -> {
                        optionButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_incorrect)
                        optionButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                    // Pilihan lain yang tidak dipilih
                    else -> {
                        optionButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.grey_button_background_d9)
                        optionButton.setTextColor(ContextCompat.getColor(this, R.color.black))
                    }
                }

                optionsContainer.addView(optionButton)
            }

            container.addView(questionView)
        }
    }
}