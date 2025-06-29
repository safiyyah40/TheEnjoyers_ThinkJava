package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterUsername : AppCompatActivity() {

    private lateinit var usernameField: EditText
    private lateinit var emailField: EditText
    private lateinit var nextButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_username)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi View
        usernameField = findViewById(R.id.editTextText)
        emailField = findViewById(R.id.editTextText1)
        nextButton = findViewById(R.id.button6)
        cancelButton = findViewById(R.id.button7)

        cancelButton.setOnClickListener {
            finish() // Tutup halaman ini
        }

        nextButton.setOnClickListener {
            handleNext()
        }
    } //proses

    private fun handleNext() {
        val username = usernameField.text.toString().trim()
        val email = emailField.text.toString().trim()

        // Validasi input
        if (username.isEmpty()) {
            usernameField.error = "Username tidak boleh kosong"
            return
        }

        if (email.isEmpty()) {
            emailField.error = "Email tidak boleh kosong"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.error = "Format email tidak valid"
            return
        }

        // Lanjut ke halaman RegisterPassword dengan membawa data
        val intent = Intent(this, RegisterPassword::class.java)
        intent.putExtra("username", username)
        intent.putExtra("email", email)
        startActivity(intent)
    }
}
