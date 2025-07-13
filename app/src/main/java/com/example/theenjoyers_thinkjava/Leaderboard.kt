package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Leaderboard : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
    }

    private val TAG = "LEADERBOARD_ACTIVITY"

    private lateinit var leaderboardTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewRanks4to10: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var leaderboardAdapter4to10: LeaderboardAdapter
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        leaderboardTitle = findViewById(R.id.tv_leaderboard_title)
        recyclerView = findViewById(R.id.rv_leaderboard)
        recyclerViewRanks4to10 = findViewById(R.id.rv_leaderboard_4_to_10)
        loadingIndicator = findViewById(R.id.leaderboard_loading_indicator)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewRanks4to10.layoutManager = LinearLayoutManager(this)

        leaderboardAdapter = LeaderboardAdapter(emptyList())
        leaderboardAdapter4to10 = LeaderboardAdapter(emptyList())
        recyclerView.adapter = leaderboardAdapter
        recyclerViewRanks4to10.adapter = leaderboardAdapter4to10

        // Logika untuk memuat leaderboard berdasarkan Intent
        val categoryFromIntent = intent.getStringExtra(EXTRA_CATEGORY)
        if (categoryFromIntent != null) {
            // Jika datang dari ScoreActivity, muat kategori spesifik
            loadLeaderboardFromFirestore(categoryFromIntent)
        } else {
            // Jika tidak, muat kategori default ("Variabel")
            loadLeaderboardFromFirestore("Variabel")
        }

        setupCategoryButtons()
        setupBottomNavigation()
    }

    private fun setupCategoryButtons() {
        findViewById<Button>(R.id.btn_category_variable).setOnClickListener { loadLeaderboardFromFirestore("Variabel") }
        findViewById<Button>(R.id.btn_category_inheritance).setOnClickListener { loadLeaderboardFromFirestore("Inheritance") }
        findViewById<Button>(R.id.btn_category_array).setOnClickListener { loadLeaderboardFromFirestore("Array") }
        findViewById<Button>(R.id.btn_category_looping).setOnClickListener { loadLeaderboardFromFirestore("Looping") }
    }

    private fun loadLeaderboardFromFirestore(category: String) {
        leaderboardTitle.text = "Leaderboard $category Java"
        clearLeaderboardView()
        loadingIndicator.visibility = View.VISIBLE

        db.collection("leaderboard")
            .whereEqualTo("category", category)
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { documents ->
                loadingIndicator.visibility = View.GONE
                if (documents.isEmpty) {
                    Toast.makeText(this, "Jadilah yang pertama di leaderboard ini!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val currentUserId = auth.currentUser?.uid
                val leaderboardList = documents.map { doc ->
                    LeaderboardEntry(
                        rank = 0,
                        name = doc.getString("username") ?: "Tanpa Nama",
                        score = (doc.getLong("score") ?: 0).toInt(),
                        isCurrentUser = (doc.getString("userId") == currentUserId)
                    )
                }.mapIndexed { index, entry ->
                    entry.copy(rank = index + 1)
                }

                val top3 = leaderboardList.take(3)
                val ranks4to10 = leaderboardList.drop(3).take(7)
                val restOfTheList = leaderboardList.drop(10)

                populateTop3(top3)
                leaderboardAdapter4to10.updateData(ranks4to10)
                leaderboardAdapter.updateData(restOfTheList)
            }
            .addOnFailureListener { exception ->
                loadingIndicator.visibility = View.GONE
                Log.e(TAG, "Gagal memuat leaderboard: ", exception)
                Toast.makeText(this, "Error: Gagal memuat data. Cek Logcat.", Toast.LENGTH_LONG).show()
            }
    }

    private fun populateTop3(top3List: List<LeaderboardEntry>) {
        val rank1Layout = findViewById<View>(R.id.layout_rank_1)
        val rank2Layout = findViewById<View>(R.id.layout_rank_2)
        val rank3Layout = findViewById<View>(R.id.layout_rank_3)

        rank1Layout.visibility = View.INVISIBLE
        rank2Layout.visibility = View.INVISIBLE
        rank3Layout.visibility = View.INVISIBLE

        if (top3List.isNotEmpty()) {
            rank1Layout.visibility = View.VISIBLE
            rank1Layout.findViewById<TextView>(R.id.tv_name_rank_1).text = top3List[0].name
            rank1Layout.findViewById<TextView>(R.id.tv_score_rank_1).text = "${top3List[0].score} pts"
            rank1Layout.findViewById<ImageView>(R.id.iv_rank_1).setImageResource(R.drawable.ic_default_avatar)
        }

        if (top3List.size >= 2) {
            rank2Layout.visibility = View.VISIBLE
            rank2Layout.findViewById<TextView>(R.id.tv_name_rank_2).text = top3List[1].name
            rank2Layout.findViewById<TextView>(R.id.tv_score_rank_2).text = "${top3List[1].score} pts"
            rank2Layout.findViewById<ImageView>(R.id.iv_rank_2).setImageResource(R.drawable.ic_default_avatar)
        }

        if (top3List.size >= 3) {
            rank3Layout.visibility = View.VISIBLE
            rank3Layout.findViewById<TextView>(R.id.tv_name_rank_3).text = top3List[2].name
            rank3Layout.findViewById<TextView>(R.id.tv_score_rank_3).text = "${top3List[2].score} pts"
            rank3Layout.findViewById<ImageView>(R.id.iv_rank_3).setImageResource(R.drawable.ic_default_avatar)
        }
    }

    private fun clearLeaderboardView() {
        populateTop3(emptyList())
        if(::leaderboardAdapter.isInitialized) {
            leaderboardAdapter.updateData(emptyList())
        }
        if(::leaderboardAdapter4to10.isInitialized) {
            leaderboardAdapter4to10.updateData(emptyList())
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