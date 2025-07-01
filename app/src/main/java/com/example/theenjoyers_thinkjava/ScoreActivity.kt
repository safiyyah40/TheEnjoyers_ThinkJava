package com.example.theenjoyers_thinkjava

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.concurrent.TimeUnit

class ScoreActivity : AppCompatActivity() {

    // Key untuk intent extra
    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_TIME_TAKEN = "extra_time_taken" // Key baru untuk waktu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        // 'score' di sini adalah jumlah jawaban yang benar
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        // --- TERIMA DAN TAMPILKAN WAKTU ---
        val timeTakenInMillis = intent.getLongExtra(EXTRA_TIME_TAKEN, 0)
        val timeTakenTextView: TextView = findViewById(R.id.time_taken)
        // Format waktu menjadi MM:SS
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeTakenInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeTakenInMillis) - TimeUnit.MINUTES.toSeconds(minutes)

        timeTakenTextView.text = String.format("Waktu: %02d:%02d", minutes, seconds)

        // Data ini tidak kita perlukan untuk rumus baru, tapi bagus untuk debugging jika diperlukan
        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)

        // Ambil data untuk diteruskan ke ReviewActivity
        val questionsForReview = intent.getSerializableExtra(ReviewActivity.EXTRA_QUESTIONS)
        val userAnswersForReview = intent.getSerializableExtra(ReviewActivity.EXTRA_USER_ANSWERS)
        val categoryName = intent.getStringExtra(ReviewActivity.EXTRA_CATEGORY_NAME)

        // setiap jawaban benar bernilai 4 poin (100 / 25 = 4).
        val finalScore = score * 4

        // Tampilkan skor yang sudah dikonversi ke skala 100
        findViewById<TextView>(R.id.tv_score_value).text = finalScore.toString()
        // Tombol kembali ke menu utama
        findViewById<Button>(R.id.btn_back_to_main_menu).setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Tombol "Lihat Ulasan Kuis"
        findViewById<Button>(R.id.btn_review_quiz).setOnClickListener {
            val reviewIntent = Intent(this, ReviewActivity::class.java).apply {
                // Teruskan data yang sudah diterima ke ReviewActivity
                putExtra(ReviewActivity.EXTRA_QUESTIONS, questionsForReview)
                putExtra(ReviewActivity.EXTRA_USER_ANSWERS, userAnswersForReview)
                putExtra(ReviewActivity.EXTRA_CATEGORY_NAME, categoryName)
            }
            startActivity(reviewIntent)
        }
    }
}