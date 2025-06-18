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

class RegisterUsername : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_username)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val usernameField = findViewById<EditText>(R.id.editTextText)
        val emailField = findViewById<EditText>(R.id.editTextText1)
        val nextButton = findViewById<Button>(R.id.button6)
        val cancelButton = findViewById<Button>(R.id.button7)

        cancelButton.setOnClickListener {
            // Fungsi tombol batal: kembali ke layar sebelumnya
            finish()
        }

        nextButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val email = emailField.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Nama pengguna tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Bisa ditambahkan validasi email lebih lanjut jika perlu

            // Kirim data username dan email ke RegisterPassword
            val intent = Intent(this, RegisterPassword::class.java)
            intent.putExtra("username", username)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }
}
