package com.example.theenjoyers_thinkjava

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class EditProfile : AppCompatActivity() {

    private lateinit var editTextNamaLengkap: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextAge: EditText
    private lateinit var btnSimpan: Button
    private lateinit var backButton: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        editTextNamaLengkap = findViewById(R.id.editTextNamaLengkap)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextAge = findViewById(R.id.editTextAge)
        btnSimpan = findViewById(R.id.Simpan)
        backButton = findViewById(R.id.btn_back)
        backButton.setOnClickListener { finish() }

        val userId = currentUser.uid

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    editTextNamaLengkap.setText(document.getString("fullName") ?: "")
                    editTextUsername.setText(document.getString("username") ?: "")
                    editTextEmail.setText(document.getString("email") ?: "")
                    editTextAge.setText(document.getLong("age")?.toString() ?: "")
                }
            }

        btnSimpan.setOnClickListener {
            val namaLengkap = editTextNamaLengkap.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val age = editTextAge.text.toString().trim().toIntOrNull()

            if (namaLengkap.isEmpty() || username.isEmpty() || email.isEmpty() || age == null) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userData = hashMapOf<String, Any>(
                "fullName" to namaLengkap,
                "username" to username,
                "email" to email,
                "age" to age
            )

            updateUserFirestore(userId, userData)
        }
    }

    private fun updateUserFirestore(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                val newUsername = userData["username"] as? String ?: ""

                if (newUsername.isNotEmpty()) {
                    val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    sharedPref.edit().putString("username", newUsername).apply()
                }

                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update profil", Toast.LENGTH_SHORT).show()
            }
    }
}