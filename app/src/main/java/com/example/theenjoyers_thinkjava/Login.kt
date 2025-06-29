package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
=======
import android.widget.Button
import android.widget.EditText
>>>>>>> 1e8331fdebe9e4ccb2419d12ae6b2c2700e3084a
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore
=======
import com.google.firebase.firestore.FirebaseFirestore // Impor FirebaseFirestore
>>>>>>> 1e8331fdebe9e4ccb2419d12ae6b2c2700e3084a

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
<<<<<<< HEAD
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var forgotPasswordButton: Button

    private var isPasswordVisible = false
=======
    private lateinit var firestore: FirebaseFirestore // Deklarasikan firestore
>>>>>>> 1e8331fdebe9e4ccb2419d12ae6b2c2700e3084a

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
<<<<<<< HEAD
        firestore = FirebaseFirestore.getInstance()

        usernameField = findViewById(R.id.editTextText2)
        passwordField = findViewById(R.id.editTextText3)
        loginButton = findViewById(R.id.button3)
        togglePasswordButton = findViewById(R.id.buttonTogglePasswordLogin)
        forgotPasswordButton = findViewById(R.id.button9)

        // ==========================
        // Toggle Password Visibility
        // ==========================
        togglePasswordButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.visibilty_off) // Ganti dengan icon mata dicoret
            } else {
                passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.visibility) // Ganti dengan icon mata biasa
            }
            // Supaya cursor tetap di akhir teks
            passwordField.setSelection(passwordField.text.length)
        }

        // ================================
        // Tombol Lupa Password ke Activity
        // ================================
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)  // <-- Ganti kalau nama activity-mu beda
            startActivity(intent)
        }

        // =================
        // Tombol Login
        // =================
        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userDoc = documents.documents[0]
                        val email = userDoc.getString("email")

                        if (!email.isNullOrEmpty()) {
                            loginWithEmail(email, password, username)
                        } else {
                            Toast.makeText(this, "Email untuk username ini tidak ditemukan.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Username tidak ditemukan.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mencari username: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loginWithEmail(email: String, password: String, username: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Selamat datang, $username!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Dashboard::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
=======
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
>>>>>>> 1e8331fdebe9e4ccb2419d12ae6b2c2700e3084a
