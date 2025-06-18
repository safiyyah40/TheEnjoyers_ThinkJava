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
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val email = intent.getStringExtra("email")
        val username = intent.getStringExtra("username")

        val passwordField = findViewById<EditText>(R.id.editTextText4)
        val submitButton = findViewById<Button>(R.id.button5)
        val backButton = findViewById<Button>(R.id.button8)

        backButton.setOnClickListener {
            finish()
        }

        submitButton.setOnClickListener {
            val password = passwordField.text.toString().trim()

            if (password.isEmpty()) {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNullOrEmpty() || username.isNullOrEmpty()) {
                Toast.makeText(this, "Data email atau username tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek apakah email sudah terdaftar
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        // Email belum terdaftar, lanjut buat akun
                        createAccount(email, password, username)
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        // Email sudah terdaftar
                        Toast.makeText(this, "Email sudah terdaftar, silakan login.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Gagal mengecek email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun createAccount(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "uid" to userId,
                            "email" to email,
                            "username" to username
                        )
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                // BARIS INI AKAN DIJALANKAN HANYA JIKA PENYIMPANAN KE FIRESTORE SUKSES
                                startActivity(Intent(this, Login::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // JIKA MASUK KE SINI, BERARTI ADA ERROR SAAT MENYIMPAN KE FIRESTORE
                                // PERHATIKAN PESAN INI: ${e.message}
                                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "User ID tidak ditemukan setelah pendaftaran.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Ini adalah error saat membuat akun di Firebase Authentication
                    Toast.makeText(this, "Gagal mendaftar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}