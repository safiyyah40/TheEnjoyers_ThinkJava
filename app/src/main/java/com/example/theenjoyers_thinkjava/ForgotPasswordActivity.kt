package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var buttonNext: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailEditText = findViewById(R.id.editTextEmailForgot)
        buttonNext = findViewById(R.id.buttonNextForgot)
        auth = FirebaseAuth.getInstance()

        buttonNext.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek email di Firebase
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (!signInMethods.isNullOrEmpty()) {
                            // Email terdaftar
                            Toast.makeText(this, "Email ditemukan! Lanjut ke OTP.", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, OtpVerificationActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Email belum terdaftar di aplikasi.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Terjadi kesalahan: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
