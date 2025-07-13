package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var buttonNext: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailEditText = findViewById(R.id.et_email)
        buttonNext = findViewById(R.id.btn_next)
        backButton = findViewById(R.id.button4)
        backButton.setOnClickListener { finish() }
        auth = FirebaseAuth.getInstance()

        buttonNext.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            // Validasi Email
            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim Link Reset Password ke Email
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Link reset password sudah dikirim ke email", Toast.LENGTH_LONG).show()

                        // Lanjut ke halaman OTP
                        val intent = Intent(this, OtpVerificationActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)

                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Terjadi kesalahan."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}