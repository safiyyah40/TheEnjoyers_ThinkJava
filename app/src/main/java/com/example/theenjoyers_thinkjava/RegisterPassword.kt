package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

    private lateinit var passwordField: EditText
    private lateinit var toggleButton: ImageButton
    private lateinit var submitButton: Button
    private lateinit var backButton: Button

    private var email: String? = null
    private var username: String? = null
    private var isPasswordVisible = false

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

        email = intent.getStringExtra("email")
        username = intent.getStringExtra("username")

        passwordField = findViewById(R.id.editTextText4)
        toggleButton = findViewById(R.id.buttonTogglePassword)
        submitButton = findViewById(R.id.button5)
        backButton = findViewById(R.id.button8)

        // Tombol kembali
        backButton.setOnClickListener {
            finish()
        }

        // Tombol Submit
        submitButton.setOnClickListener {
            handleRegister()
        }

        // Tombol toggle visibilitas password (ikon mata)
        toggleButton.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            // Password tampak
            passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleButton.setImageResource(R.drawable.visibilty_off)  // Icon mata tertutup
        } else {
            // Password disembunyikan
            passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleButton.setImageResource(R.drawable.visibility)  // Icon mata biasa
        }
        // Agar cursor tetap di akhir teks
        passwordField.setSelection(passwordField.text.length)
    }

    private fun handleRegister() {
        val password = passwordField.text.toString().trim()

        // Validasi input
        if (email.isNullOrEmpty() || username.isNullOrEmpty()) {
            Toast.makeText(this, "Email atau Username hilang, silakan ulangi proses", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            passwordField.error = "Password minimal 6 karakter"
            return
        }

        // Buat akun
        createAccount(email!!, password, username!!)
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
                                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, Login::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal menyimpan user data: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Gagal mendapatkan UID user setelah register.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMessage = task.exception?.message
                    if (errorMessage != null && errorMessage.contains("email address is already in use")) {
                        Toast.makeText(this, "Email sudah terdaftar, silakan login.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal membuat akun: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}
