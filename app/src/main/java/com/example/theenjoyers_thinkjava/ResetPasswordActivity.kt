package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        newPassword = findViewById(R.id.editTextNewPassword)
        confirmPassword = findViewById(R.id.editTextConfirmPassword)
        submitButton = findViewById(R.id.buttonSubmitReset)

        submitButton.setOnClickListener {
            val pass = newPassword.text.toString()
            val confirmPass = confirmPassword.text.toString()

            if (pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    Toast.makeText(this, "Password berhasil direset!", Toast.LENGTH_SHORT).show()
                    // Kembali ke halaman login
                    startActivity(Intent(this, Login::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}