package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var forgotPasswordButton: Button
    private lateinit var backButton: Button

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        usernameField = findViewById(R.id.editTextText2)
        passwordField = findViewById(R.id.editTextText3)
        loginButton = findViewById(R.id.button3)
        backButton = findViewById(R.id.button4)
        backButton.setOnClickListener { finish() }
        togglePasswordButton = findViewById(R.id.buttonTogglePasswordLogin)
        forgotPasswordButton = findViewById(R.id.button9)

        togglePasswordButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordField.inputType = if (isPasswordVisible) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordField.setSelection(passwordField.text.length)
        }

        forgotPasswordButton.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginWithUsername(username, password)
        }
    }

    private fun loginWithUsername(username: String, password: String) {
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
                        AlertDialog.Builder(this)
                            .setTitle("Login Gagal")
                            .setMessage("Email yang terkait dengan username ini tidak ditemukan.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Login Gagal")
                        .setMessage("Username tidak ditemukan. Silakan periksa kembali.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("LOGIN_FIRESTORE", "Gagal mengambil data username", e)
                AlertDialog.Builder(this)
                    .setTitle("Kesalahan Server")
                    .setMessage("Terjadi kesalahan saat mengakses data. Silakan coba lagi nanti.")
                    .setPositiveButton("OK", null)
                    .show()
            }
    }


    private fun loginWithEmail(email: String, password: String, username: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Simpan username ke SharedPreferences
                    val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    sharedPref.edit().putString("username", username).apply()

                    Toast.makeText(this, "Selamat datang, $username!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Dashboard::class.java))
                    finish()
                } else {
                    // Tampilkan pesan error dalam bahasa Indonesia
                    val errorCode = task.exception?.message ?: ""
                    val message = when {
                        errorCode.contains("password is invalid", true) -> "Username atau kata sandi salah."
                        errorCode.contains("no user record", true) -> "Akun dengan username ini tidak ditemukan."
                        else -> "Terjadi kesalahan saat masuk. Silakan coba lagi."
                    }

                    AlertDialog.Builder(this)
                        .setTitle("Login Gagal")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show()

                    Log.e("LOGIN_AUTH", "Gagal login", task.exception)
                }
            }
    }

}
