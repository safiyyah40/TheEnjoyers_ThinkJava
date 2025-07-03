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

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var forgotPasswordButton: Button

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        usernameField = findViewById(R.id.editTextText2)
        passwordField = findViewById(R.id.editTextText3)
        loginButton = findViewById(R.id.button3)
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
                        Toast.makeText(this, "Email untuk username ini tidak ditemukan.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Username tidak ditemukan.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("LOGIN_FIRESTORE", "Gagal mengambil data username", e)
                Toast.makeText(this, "Gagal mencari username: ${e.message}", Toast.LENGTH_LONG).show()
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
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    Log.e("LOGIN_AUTH", "Gagal login", task.exception)
                }
            }
    }
}
