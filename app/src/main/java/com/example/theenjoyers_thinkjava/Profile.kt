package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupBottomNavigation()
        setupEditProfileButton()
        setupLogoutButton()
        showUsername()
    }

    // Ambil username dari SharedPreferences
    private fun showUsername() {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "Pengguna") ?: "Pengguna"
        val usernameTextView = findViewById<TextView>(R.id.text_username)
        usernameTextView.text = username
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
            // Hapus username dari SharedPreferences
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
