package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var submitButton: Button
    private lateinit var backButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        newPassword = findViewById(R.id.editTextNewPassword)
        confirmPassword = findViewById(R.id.editTextConfirmPassword)
        submitButton = findViewById(R.id.buttonSubmitReset)
        backButton = findViewById(R.id.btn_back)

        backButton.setOnClickListener { finish() }
        auth = FirebaseAuth.getInstance()

        submitButton.setOnClickListener {
            val pass = newPassword.text.toString()
            val confirmPass = confirmPassword.text.toString()

            if (pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    val user: FirebaseUser? = auth.currentUser

                    if (user != null) {
                        user.updatePassword(pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Password berhasil direset!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, Login::class.java))
                                    finishAffinity()
                                } else {
                                    Toast.makeText(this, "Gagal reset password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "User belum login. Silakan login ulang atau gunakan link reset email.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Login::class.java))
                        finishAffinity()
                    }

                } else {
                    Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
