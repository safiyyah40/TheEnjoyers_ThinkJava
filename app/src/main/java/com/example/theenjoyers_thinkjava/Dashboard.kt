package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dashboard : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupUsername()
        setupQuizButtons()
        setupBottomNavigation()
    }

    private fun setupUsername() {
        val textViewUsername: TextView = findViewById(R.id.textView2)

        // Ambil dari SharedPreferences
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "Guest") ?: "Guest"

        textViewUsername.text = username
    }

    private fun setupQuizButtons() {
        findViewById<Button>(R.id.btnMulai).setOnClickListener {
            startQuiz("Variabel")
        }
        findViewById<Button>(R.id.btnMulai1).setOnClickListener {
            startQuiz("Inheritance")
        }
        findViewById<Button>(R.id.btnMulai2).setOnClickListener {
            startQuiz("Array")
        }
        findViewById<Button>(R.id.btnMulai3).setOnClickListener {
            startQuiz("Looping")
        }
    }

    private fun startQuiz(category: String) {
        Toast.makeText(this, "Membuka kuis $category...", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, QuestionActivity::class.java).apply {
            putExtra(EXTRA_CATEGORY, category)
        }
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)
        bottomNavigationView.selectedItemId = R.id.menu_dashboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> true
                R.id.menu_leaderboard -> {
                    startActivity(Intent(this, Leaderboard::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
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
