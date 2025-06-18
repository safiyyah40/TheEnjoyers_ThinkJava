package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // Impor FirebaseFirestore

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore // Deklarasikan firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Inisialisasi firestore

        val emailField = findViewById<EditText>(R.id.editTextText2) // Pastikan ID ini benar
        val passwordField = findViewById<EditText>(R.id.editTextText3) // Pastikan ID ini benar
        val loginButton = findViewById<Button>(R.id.button3) // Pastikan ID ini benar

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let { firebaseUser ->
                                // Pengguna berhasil login di Firebase Auth, sekarang ambil username dari Firestore
                                firestore.collection("users").document(firebaseUser.uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            // Ambil nilai 'username' dari dokumen Firestore
                                            val username = document.getString("username")

                                            if (username != null) {
                                                Toast.makeText(this, "Selamat datang, $username!", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, Dashboard::class.java)
                                                // Kirim username ke Dashboard dengan kunci "username"
                                                intent.putExtra("username", username)
                                                startActivity(intent)
                                                finish() // Tutup aktivitas Login
                                            } else {
                                                // Jika field 'username' tidak ada di Firestore (kemungkinan data lama atau salah)
                                                Toast.makeText(this, "Username tidak ditemukan di data pengguna.", Toast.LENGTH_LONG).show()
                                                startActivity(Intent(this, Dashboard::class.java))
                                                finish()
                                            }
                                        } else {
                                            // Dokumen pengguna (data username, email, dll.) tidak ditemukan di koleksi 'users'
                                            Toast.makeText(this, "Data pengguna tidak ditemukan di Firestore.", Toast.LENGTH_LONG).show()
                                            startActivity(Intent(this, Dashboard::class.java))
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Gagal mengambil data dari Firestore (misal: masalah koneksi atau aturan keamanan)
                                        Toast.makeText(this, "Gagal mengambil data pengguna: ${e.message}", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(this, Dashboard::class.java))
                                        finish()
                                    }
                            }
                        } else {
                            // Login gagal di Firebase Authentication (misal: email/password salah)
                            Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Harap isi email dan kata sandi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}