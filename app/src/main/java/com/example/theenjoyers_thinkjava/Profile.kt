package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
    }
    // Bottom Navbar
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)
        bottomNavigationView.selectedItemId = R.id.menu_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> {
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_leaderboard -> {
                    val intent = Intent(this, Leaderboard::class.java)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.menu_profile -> {
                    true
                }
                else -> false
            }
        }
    }
}