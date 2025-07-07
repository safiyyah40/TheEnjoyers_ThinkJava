package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Leaderboard : AppCompatActivity() {

    private lateinit var leaderboardTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inisialisasi Views
        leaderboardTitle = findViewById(R.id.tv_leaderboard_title)
        recyclerView = findViewById(R.id.rv_leaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Muat data default (misal: Variabel) saat pertama kali dibuka
        loadLeaderboardData("Variabel")

        setupCategoryButtons()
        setupBottomNavigation()
    }

    private fun setupCategoryButtons() {
        findViewById<Button>(R.id.btn_category_variable).setOnClickListener {
            loadLeaderboardData("Variabel")
        }
        findViewById<Button>(R.id.btn_category_inheritance).setOnClickListener {
            loadLeaderboardData("Inheritance")
        }
        findViewById<Button>(R.id.btn_category_array).setOnClickListener {
            loadLeaderboardData("Array")
        }
        findViewById<Button>(R.id.btn_category_looping).setOnClickListener {
            loadLeaderboardData("Looping")
        }
    }

    /**
     * Fungsi yang mengambil data dari Firestore, bukan lagi data dummy.
     */
    private fun loadLeaderboardData(category: String) {
        leaderboardTitle.text = "Leaderboard $category Java"

        db.collection("leaderboard")
            .whereEqualTo("category", category)
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(100) // Batasi untuk 100 teratas
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Tampilkan pesan jika leaderboard kosong
                    Toast.makeText(this, "Leaderboard untuk $category masih kosong.", Toast.LENGTH_SHORT).show()
                    clearLeaderboardView() // Fungsi untuk membersihkan tampilan
                    return@addOnSuccessListener
                }

                val leaderboardList = documents.map { doc ->
                    LeaderboardEntry(
                        rank = 0, // Akan diisi di bawah
                        name = doc.getString("username") ?: "N/A",
                        score = (doc.getLong("score") ?: 0).toInt(),
                        avatarUrl = doc.getString("photoUrl") ?: "",
                        isCurrentUser = (doc.getString("userId") == auth.currentUser?.uid) // Tandai jika ini user saat ini
                    )
                }.mapIndexed { index, entry ->
                    entry.copy(rank = index + 1)
                }

                // Pisahkan Top 3 dan sisanya
                val top3 = leaderboardList.take(3)
                val rest = leaderboardList.drop(3)

                populateTop3(top3)
                leaderboardAdapter = LeaderboardAdapter(rest)
                recyclerView.adapter = leaderboardAdapter

            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Gagal memuat leaderboard", exception)
                Toast.makeText(this, "Gagal memuat data.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearLeaderboardView() {
        // Kosongkan tampilan Top 3
        findViewById<TextView>(R.id.tv_name_rank_1).text = "-"
        findViewById<TextView>(R.id.tv_score_rank_1).text = ""
        findViewById<ImageView>(R.id.iv_rank_1).setImageResource(R.drawable.ic_default_avatar)
        // Lakukan hal yang sama untuk rank 2 dan 3
        findViewById<TextView>(R.id.tv_name_rank_2).text = "-"
        findViewById<TextView>(R.id.tv_score_rank_2).text = ""
        findViewById<ImageView>(R.id.iv_rank_2).setImageResource(R.drawable.ic_default_avatar)

        findViewById<TextView>(R.id.tv_name_rank_3).text = "-"
        findViewById<TextView>(R.id.tv_score_rank_3).text = ""
        findViewById<ImageView>(R.id.iv_rank_3).setImageResource(R.drawable.ic_default_avatar)

        // Kosongkan RecyclerView
        recyclerView.adapter = LeaderboardAdapter(emptyList())
    }


    private fun populateTop3(top3List: List<LeaderboardEntry>) {
        // Pastikan view di-reset jika data tidak lengkap
        clearLeaderboardView()

        // Juara 1
        if (top3List.isNotEmpty()) {
            findViewById<TextView>(R.id.tv_name_rank_1).text = top3List[0].name
            findViewById<TextView>(R.id.tv_score_rank_1).text = "${top3List[0].score} pts"
            val ivRank1 = findViewById<ImageView>(R.id.iv_rank_1)
            Glide.with(this).load(top3List[0].avatarUrl).placeholder(R.drawable.ic_default_avatar).circleCrop().into(ivRank1)
        }

        // Juara 2
        if (top3List.size >= 2) {
            findViewById<TextView>(R.id.tv_name_rank_2).text = top3List[1].name
            findViewById<TextView>(R.id.tv_score_rank_2).text = "${top3List[1].score} pts"
            val ivRank2 = findViewById<ImageView>(R.id.iv_rank_2)
            Glide.with(this).load(top3List[1].avatarUrl).placeholder(R.drawable.ic_default_avatar).circleCrop().into(ivRank2)
        }

        // Juara 3
        if (top3List.size >= 3) {
            findViewById<TextView>(R.id.tv_name_rank_3).text = top3List[2].name
            findViewById<TextView>(R.id.tv_score_rank_3).text = "${top3List[2].score} pts"
            val ivRank3 = findViewById<ImageView>(R.id.iv_rank_3)
            Glide.with(this).load(top3List[2].avatarUrl).placeholder(R.drawable.ic_default_avatar).circleCrop().into(ivRank3)
        }
    }


    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)
        bottomNavigationView.selectedItemId = R.id.menu_leaderboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> {
                    startActivity(Intent(this, Dashboard::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.menu_leaderboard -> true
                R.id.menu_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}