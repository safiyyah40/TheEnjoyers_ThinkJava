package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class ScoreActivity : AppCompatActivity() {

    // Key untuk intent extra
    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_TIME_TAKEN = "extra_time_taken"
    }

    // Tambahkan properti untuk Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 'score' di sini adalah jumlah jawaban yang benar
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val timeTakenInMillis = intent.getLongExtra(EXTRA_TIME_TAKEN, 0)
        val timeTakenTextView: TextView = findViewById(R.id.time_taken)

        // Format waktu menjadi MM:SS
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeTakenInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeTakenInMillis) - TimeUnit.MINUTES.toSeconds(minutes)
        timeTakenTextView.text = String.format("Waktu: %02d:%02d", minutes, seconds)

        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 0)
        val questionsForReview = intent.getSerializableExtra(ReviewActivity.EXTRA_QUESTIONS)
        val userAnswersForReview = intent.getSerializableExtra(ReviewActivity.EXTRA_USER_ANSWERS)
        val categoryName = intent.getStringExtra(ReviewActivity.EXTRA_CATEGORY_NAME) ?: "Kuis"

        // setiap jawaban benar bernilai 4 poin (100 / 25 = 4).
        val finalScore = score * 4

        // Tampilkan skor
        findViewById<TextView>(R.id.tv_score_value).text = finalScore.toString()

        // Panggil fungsi untuk menyimpan skor
        if (auth.currentUser != null) {
            saveScoreToFirestore(categoryName, finalScore)
        }

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
                putExtra(ReviewActivity.EXTRA_QUESTIONS, questionsForReview)
                putExtra(ReviewActivity.EXTRA_USER_ANSWERS, userAnswersForReview)
                putExtra(ReviewActivity.EXTRA_CATEGORY_NAME, categoryName)
            }
            startActivity(reviewIntent)
        }
    }

    /**
     * Fungsi baru untuk menyimpan data skor ke Firestore.
     * Skor akan disimpan di sub-koleksi 'scores' di dalam dokumen user.
     */
    private fun saveScoreToFirestore(category: String, score: Int) {
        val userId = auth.currentUser?.uid ?: return

        // Buat data skor yang akan disimpan
        val scoreData = hashMapOf(
            "category" to category,
            "score" to score,
            "timestamp" to FieldValue.serverTimestamp() // Gunakan timestamp server
        )

        // Simpan ke sub-koleksi 'scores'
        db.collection("users").document(userId)
            .collection("scores")
            .add(scoreData)
            .addOnSuccessListener {
                Log.d("Firestore", "Skor berhasil disimpan untuk kategori: $category")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal menyimpan skor", e)
                Toast.makeText(this, "Gagal menyimpan skor ke profil.", Toast.LENGTH_SHORT).show()
            }
    }
}