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

    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_TIME_TAKEN = "extra_time_taken"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Ambil data dari intent
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val categoryName = intent.getStringExtra(ReviewActivity.EXTRA_CATEGORY_NAME) ?: "Kuis"
        val finalScore = score * 4 // Konversi ke skala 100

        // Tampilkan skor dan waktu
        displayScoreAndTime(finalScore)

        // Simpan skor jika pengguna sudah login
        if (auth.currentUser != null) {
            saveScoresToDatabase(categoryName, finalScore)
        }

        // Setup tombol-tombol
        setupButtons()
    }

    private fun displayScoreAndTime(finalScore: Int) {
        findViewById<TextView>(R.id.tv_score_value).text = finalScore.toString()

        val timeTakenInMillis = intent.getLongExtra(EXTRA_TIME_TAKEN, 0)
        val timeTakenTextView: TextView = findViewById(R.id.time_taken)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeTakenInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeTakenInMillis) - TimeUnit.MINUTES.toSeconds(minutes)
        timeTakenTextView.text = String.format("Waktu: %02d:%02d", minutes, seconds)
    }

    /**
     * Menyimpan skor ke DUA lokasi di Firestore:
     * 1. Ke profil pengguna untuk statistik pribadi.
     * 2. Ke koleksi 'leaderboard' global untuk pemeringkatan.
     */
    private fun saveScoresToDatabase(category: String, score: Int) {
        val userId = auth.currentUser?.uid ?: return

        // Ambil data profil pengguna (username & foto) terlebih dahulu
        db.collection("users").document(userId).get().addOnSuccessListener { userDocument ->
            val username = userDocument.getString("username") ?: "Anonymous"
            val photoUrl = userDocument.getString("photoUrl") ?: ""

            // 1. Simpan ke riwayat skor di profil pengguna
            val profileScoreData = hashMapOf(
                "category" to category,
                "score" to score,
                "timestamp" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(userId).collection("scores").add(profileScoreData)
                .addOnSuccessListener { Log.d("Firestore", "Skor berhasil disimpan ke profil.") }

            // 2. Simpan atau perbarui skor di koleksi leaderboard global
            // Dokumen ID unik memastikan setiap pengguna hanya punya 1 entri per kategori
            val leaderboardDocId = "${userId}_${category}"
            val leaderboardData = hashMapOf(
                "userId" to userId,
                "username" to username,
                "photoUrl" to photoUrl,
                "category" to category,
                "score" to score,
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Gunakan .set() untuk membuat atau menimpa entri yang ada
            db.collection("leaderboard").document(leaderboardDocId).set(leaderboardData)
                .addOnSuccessListener { Log.d("Firestore", "Skor berhasil disimpan ke leaderboard.") }

        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengambil data profil untuk leaderboard.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtons() {
        val questionsForReview = intent.getSerializableExtra(ReviewActivity.EXTRA_QUESTIONS)
        val userAnswersForReview = intent.getSerializableExtra(ReviewActivity.EXTRA_USER_ANSWERS)
        val categoryName = intent.getStringExtra(ReviewActivity.EXTRA_CATEGORY_NAME)

        findViewById<Button>(R.id.btn_back_to_main_menu).setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btn_review_quiz).setOnClickListener {
            val reviewIntent = Intent(this, ReviewActivity::class.java).apply {
                putExtra(ReviewActivity.EXTRA_QUESTIONS, questionsForReview)
                putExtra(ReviewActivity.EXTRA_USER_ANSWERS, userAnswersForReview)
                putExtra(ReviewActivity.EXTRA_CATEGORY_NAME, categoryName)
            }
            startActivity(reviewIntent)
        }
    }
}