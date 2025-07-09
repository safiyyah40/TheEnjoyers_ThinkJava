package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUsername : AppCompatActivity() {

    private lateinit var fullNameField: EditText
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

        fullNameField = findViewById(R.id.editTextFullName)
        usernameField = findViewById(R.id.editTextText)
        emailField = findViewById(R.id.editTextText1)
        nextButton = findViewById(R.id.button6)
        cancelButton = findViewById(R.id.button7)

        cancelButton.setOnClickListener {
            finish()
        }

        nextButton.setOnClickListener {
            handleNext()
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun handleNext() {
        val fullName = fullNameField.text.toString().trim()
        val username = usernameField.text.toString().trim()
        val email = emailField.text.toString().trim()

        when {
            fullName.isEmpty() -> {
                showAlert("Validasi Gagal", "Nama lengkap tidak boleh kosong.")
                return
            }
            username.isEmpty() -> {
                showAlert("Validasi Gagal", "Username tidak boleh kosong.")
                return
            }
            username.length < 4 -> {
                showAlert("Validasi Gagal", "Username minimal 4 karakter.")
                return
            }
            email.isEmpty() -> {
                showAlert("Validasi Gagal", "Email tidak boleh kosong.")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showAlert("Validasi Gagal", "Format email tidak valid.")
                return
            }
            else -> {
                checkUsernameAndEmail(username, email, fullName)
            }
        }
    }

    private fun checkUsernameAndEmail(username: String, email: String, fullName: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { usernameDocs ->
                if (!usernameDocs.isEmpty) {
                    showAlert("Username Terpakai", "Username ini sudah digunakan oleh pengguna lain.")
                    return@addOnSuccessListener
                }

                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { emailDocs ->
                        if (!emailDocs.isEmpty) {
                            showAlert("Email Terpakai", "Email ini sudah terdaftar sebelumnya.")
                            return@addOnSuccessListener
                        }

                        // Lanjut ke halaman berikut
                        val intent = Intent(this, RegisterPassword::class.java)
                        intent.putExtra("fullName", fullName)
                        intent.putExtra("username", username)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    }
            }
            .addOnFailureListener {
                showAlert("Koneksi Gagal", "Gagal memeriksa data. Silakan periksa koneksi internet Anda.")
            }
    }
}
