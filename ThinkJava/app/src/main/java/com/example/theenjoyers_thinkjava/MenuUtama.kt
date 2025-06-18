package com.example.theenjoyers_thinkjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MenuUtama : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_utama)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Bagian Penting: Menampilkan Nama Pengguna (Kode ini sudah benar!) ---
        // Mendapatkan referensi TextView dengan ID textView2
        val textViewUsername: TextView = findViewById(R.id.textView2)

        // Mendapatkan username dari Intent.
        // Kunci "username" di sini HARUS sama persis dengan kunci yang digunakan
        // saat mengirimnya dari aktivitas Login.kt (huruf kecil semua).
        val username = intent.getStringExtra("username")

        // Memeriksa apakah username tidak null dan tidak kosong, lalu mengaturnya ke TextView
        if (!username.isNullOrEmpty()) {
            textViewUsername.text = username
        } else {
            // Jika username tidak ditemukan atau kosong, tampilkan teks default "Guest"
            textViewUsername.text = "Guest"
            Toast.makeText(this, "Nama pengguna tidak ditemukan. Menampilkan sebagai Guest.", Toast.LENGTH_LONG).show()
        }
        // --- Akhir Bagian Penting ---


        // --- Interaksi untuk tombol "Mulai" ---
        // Variabel Java Card
        val btnMulai: Button = findViewById(R.id.btnMulai)
        btnMulai.setOnClickListener {
            Toast.makeText(this, "Tombol Mulai Variabel Java diklik!", Toast.LENGTH_SHORT).show()
            // Logika untuk memulai kuis Variabel Java
        }

        // Inheritance Java Card
        val btnMulai1: Button = findViewById(R.id.btnMulai1)
        btnMulai1.setOnClickListener {
            Toast.makeText(this, "Tombol Mulai Inheritance Java diklik!", Toast.LENGTH_SHORT).show()
            // Logika untuk memulai kuis Inheritance
        }

        // Array Java Card
        val btnMulai2: Button = findViewById(R.id.btnMulai2)
        btnMulai2.setOnClickListener {
            Toast.makeText(this, "Tombol Mulai Array Java diklik!", Toast.LENGTH_SHORT).show()
            // Logika untuk memulai kuis Array
        }

        // Looping Java Card
        val btnMulai3: Button = findViewById(R.id.btnMulai3)
        btnMulai3.setOnClickListener {
            Toast.makeText(this, "Tombol Mulai Looping Java diklik!", Toast.LENGTH_SHORT).show()
            // Logika untuk memulai kuis Looping
        }
    }
}