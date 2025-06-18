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
            Toast.makeText(this, "Nama pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupQuizButtons() {
        findViewById<Button>(R.id.btnMulai).setOnClickListener {
            Toast.makeText(this, "Membuka kuis Variabel Java...", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnMulai1).setOnClickListener {
            Toast.makeText(this, "Membuka kuis Inheritance Java...", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnMulai2).setOnClickListener {
            Toast.makeText(this, "Membuka kuis Array Java...", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnMulai3).setOnClickListener {
            Toast.makeText(this, "Membuka kuis Looping Java...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)
        bottomNavigationView.selectedItemId = R.id.menu_dashboard

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> {
                    true
                }
                R.id.menu_leaderboard -> {
                    val intent = Intent(this, Leaderboard::class.java)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    // Tutup Activity saat ini agar tidak menumpuk saat tombol back ditekan
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