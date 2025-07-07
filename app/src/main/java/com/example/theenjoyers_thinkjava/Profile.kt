package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp

// Data class untuk menampung data skor dari Firestore
data class QuizScore(
    val category: String = "",
    val score: Long = 0,
    val timestamp: Timestamp? = null
)

class Profile : AppCompatActivity() {

    // Tambahkan properti untuk Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupBottomNavigation()
        setupEditProfileButton()
        setupLogoutButton()
        showUsername()

        // Panggil fungsi untuk memuat statistik skor
        loadScoreStats()
    }

    // Ambil username dari SharedPreferences
    private fun showUsername() {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "Pengguna") ?: "Pengguna"
        val usernameTextView = findViewById<TextView>(R.id.text_username)
        usernameTextView.text = username
    }

    /**
     * Fungsi baru untuk mengambil semua skor dari Firestore dan menampilkannya.
     */
    private fun loadScoreStats() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("scores")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan dari yang terbaru
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("Firestore", "Tidak ada riwayat skor.")
                    return@addOnSuccessListener
                }

                // Ubah dokumen firestore menjadi list dari data class QuizScore
                val allScores = documents.toObjects(QuizScore::class.java)

                // Kelompokkan skor berdasarkan kategori
                val scoresByCategory = allScores.groupBy { it.category }

                // Update UI untuk setiap kategori
                updateScoreCardUI("Variabel", scoresByCategory["Variabel"])
                updateScoreCardUI("Inheritance", scoresByCategory["Inheritance"])
                updateScoreCardUI("Array", scoresByCategory["Array"])
                updateScoreCardUI("Looping", scoresByCategory["Looping"])
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal mengambil statistik skor", e)
            }
    }

    /**
     * Fungsi baru untuk mengupdate satu kartu skor di UI.
     */
    private fun updateScoreCardUI(category: String, scores: List<QuizScore>?) {
        val cardViewId = when (category) {
            "Variabel" -> R.id.score_variable_layout
            "Inheritance" -> R.id.score_inheritance_layout
            "Looping" -> R.id.score_looping_layout
            "Array" -> R.id.score_array_layout
            else -> 0
        }

        if (cardViewId == 0) return

        val scoreCardView = findViewById<View>(cardViewId)
        val title = scoreCardView.findViewById<TextView>(R.id.score_title)
        val tertinggi = scoreCardView.findViewById<TextView>(R.id.score_tertinggi)
        val terbaru = scoreCardView.findViewById<TextView>(R.id.score_terbaru)
        val terendah = scoreCardView.findViewById<TextView>(R.id.score_terendah)

        title.text = category

        if (scores.isNullOrEmpty()) {
            tertinggi.text = "Nilai Tertinggi: -"
            terbaru.text = "Nilai Terbaru: -"
            terendah.text = "Nilai Terendah: -"
        } else {
            // Hitung statistik
            val maxScore = scores.maxOfOrNull { it.score } ?: 0
            val minScore = scores.minOfOrNull { it.score } ?: 0
            val latestScore = scores.first().score // List sudah diurutkan dari terbaru

            // Tampilkan di UI
            tertinggi.text = "Nilai Tertinggi: $maxScore"
            terbaru.text = "Nilai Terbaru: $latestScore"
            terendah.text = "Nilai Terendah: $minScore"
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)
        bottomNavigationView.selectedItemId = R.id.menu_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.menu_dashboard -> Intent(this, Dashboard::class.java)
                R.id.menu_leaderboard -> Intent(this, Leaderboard::class.java)
                R.id.menu_profile -> return@setOnItemSelectedListener true
                else -> return@setOnItemSelectedListener false
            }

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            true
        }
    }

    private fun setupEditProfileButton() {
        val editButton = findViewById<Button>(R.id.btn_edit_profile)
        editButton.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
        }
    }

    private fun setupLogoutButton() {
        val logoutButton = findViewById<Button>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}