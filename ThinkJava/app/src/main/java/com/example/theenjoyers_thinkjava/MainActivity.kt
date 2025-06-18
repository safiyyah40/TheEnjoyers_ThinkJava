package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Biar layout tetap responsif
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delay 2 detik lalu pindah ke homeguest
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeGuest::class.java)
            startActivity(intent)
            finish() // Supaya splash tidak bisa di-back
        }, 3000) // 2000 ms = 2 detik
    }
}
