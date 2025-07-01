package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class Leaderboard : AppCompatActivity() {

    private lateinit var leaderboardTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Inisialisasi Views
        leaderboardTitle = findViewById(R.id.tv_leaderboard_title)
        recyclerView = findViewById(R.id.rv_leaderboard)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Muat data default (misal: Variabel) saat pertama kali dibuka
        loadLeaderboardData("Variabel")

        // Setup listener untuk tombol-tombol kategori
        setupCategoryButtons()

        // Setup bottom navigation
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

    private fun loadLeaderboardData(category: String) {
        // Ganti judul leaderboard
        leaderboardTitle.text = "Leaderbord $category Java"

        // Nanti, data ini akan diambil dari database berdasarkan kategori
        // Untuk sekarang, kita pakai data dummy
        val allData = getDummyData()

        // Pisahkan data Top 3 dan sisanya
        val top3 = allData.take(3)
        val rest = allData.drop(3)

        // Tampilkan data ke UI
        populateTop3(top3)

        leaderboardAdapter = LeaderboardAdapter(rest)
        recyclerView.adapter = leaderboardAdapter
    }

    private fun populateTop3(top3List: List<LeaderboardEntry>) {
        if (top3List.size < 3) return // Hindari error jika data kurang dari 3

        // Juara 1
        findViewById<TextView>(R.id.tv_name_rank_1).text = top3List[0].name
        findViewById<TextView>(R.id.tv_score_rank_1).text = "${top3List[0].score} pts"
        val ivRank1 = findViewById<ImageView>(R.id.iv_rank_1)
        Glide.with(this).load(top3List[0].avatarUrl).placeholder(R.drawable.ic_default_avatar).circleCrop().into(ivRank1)

        // Juara 2
        findViewById<TextView>(R.id.tv_name_rank_2).text = top3List[1].name
        findViewById<TextView>(R.id.tv_score_rank_2).text = "${top3List[1].score} pts"
        val ivRank2 = findViewById<ImageView>(R.id.iv_rank_2)
        Glide.with(this).load(top3List[1].avatarUrl).placeholder(R.drawable.ic_default_avatar).circleCrop().into(ivRank2)

        // Juara 3
        findViewById<TextView>(R.id.tv_name_rank_3).text = top3List[2].name
        findViewById<TextView>(R.id.tv_score_rank_3).text = "${top3List[2].score} pts"
        val ivRank3 = findViewById<ImageView>(R.id.iv_rank_3)
        Glide.with(this).load(top3List[2].avatarUrl).placeholder(R.drawable.ic_default_avatar).circleCrop().into(ivRank3)
    }

    // Fungsi ini hanya untuk contoh. Nanti akan diganti dengan data dari Firebase.
    private fun getDummyData(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(1, "Bryan Wolf", 43, "https://i.pravatar.cc/150?img=12"),
            LeaderboardEntry(2, "Meghan Jes...", 40, "https://i.pravatar.cc/150?img=32"),
            LeaderboardEntry(3, "Alex Turner", 38, "https://i.pravatar.cc/150?img=11"),
            LeaderboardEntry(4, "Marsha Fisher", 36, "https://i.pravatar.cc/150?img=31"),
            LeaderboardEntry(5, "Juanita Cormier", 35, "https://i.pravatar.cc/150?img=40"),
            LeaderboardEntry(6, "You", 34, "https://i.pravatar.cc/150?img=5", isCurrentUser = true), // Contoh user saat ini
            LeaderboardEntry(7, "Tamara Schmidt", 33, "https://i.pravatar.cc/150?img=26"),
            LeaderboardEntry(8, "Ricardo Veum", 32, "https://i.pravatar.cc/150?img=60"),
            LeaderboardEntry(9, "Gary Sanford", 31, "https://i.pravatar.cc/150?img=52"),
            LeaderboardEntry(10, "Becky Bartell", 30, "https://i.pravatar.cc/150?img=45")
        )
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