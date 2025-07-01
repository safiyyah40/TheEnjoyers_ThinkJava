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

    // Definisikan konstanta untuk key agar tidak ada salah ketik
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
        val username = intent.getStringExtra("username")

        if (!username.isNullOrEmpty()) {
            textViewUsername.text = username
        } else {
            textViewUsername.text = "Guest"
        }
    }

    private fun setupQuizButtons() {
        // Tombol untuk kuis "Variabel"
        findViewById<Button>(R.id.btnMulai).setOnClickListener {
            startQuiz("Variabel")
        }
        // Tombol untuk kuis "Inheritance"
        findViewById<Button>(R.id.btnMulai1).setOnClickListener {
            startQuiz("Inheritance")
        }
        // Tombol untuk kuis "Array"
        findViewById<Button>(R.id.btnMulai2).setOnClickListener {
            startQuiz("Array")
        }
        // Tombol untuk kuis "Looping"
        findViewById<Button>(R.id.btnMulai3).setOnClickListener {
            startQuiz("Looping")
        }
    }

    // Fungsi bantuan untuk memulai kuis agar kode tidak berulang
    private fun startQuiz(category: String) {
        Toast.makeText(this, "Membuka kuis $category...", Toast.LENGTH_SHORT).show()

        // Membuat Intent untuk pindah ke QuestionActivity
        val intent = Intent(this, QuestionActivity::class.java).apply {
            // Menambahkan data kategori ke dalam Intent
            putExtra(EXTRA_CATEGORY, category)
        }
        // Memulai activity baru
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)
        bottomNavigationView.selectedItemId = R.id.menu_dashboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> true
                R.id.menu_leaderboard -> {
                    val intent = Intent(this, Leaderboard::class.java)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.menu_profile -> {
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}