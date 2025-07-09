package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var toggleConfirmPasswordButton: ImageButton
    private lateinit var submitButton: Button
    private lateinit var backButton: Button

    private var email: String? = null
    private var username: String? = null
    private var fullName: String? = null
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        email = intent.getStringExtra("email")
        username = intent.getStringExtra("username")
        fullName = intent.getStringExtra("fullName")

        passwordField = findViewById(R.id.editTextText4)
        confirmPasswordField = findViewById(R.id.editTextConfirmPassword)
        togglePasswordButton = findViewById(R.id.buttonTogglePassword)
        toggleConfirmPasswordButton = findViewById(R.id.buttonToggleConfirmPassword)
        submitButton = findViewById(R.id.button5)
        backButton = findViewById(R.id.button8)

        backButton.setOnClickListener { finish() }

        togglePasswordButton.setOnClickListener { togglePasswordVisibility() }
        toggleConfirmPasswordButton.setOnClickListener { toggleConfirmPasswordVisibility() }

        submitButton.setOnClickListener { handleRegister() }
    }

    private fun showPopup(title: String, message: String, onOk: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> onOk?.invoke() }
            .setCancelable(false)
            .show()
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        passwordField.inputType = if (isPasswordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        togglePasswordButton.setImageResource(if (isPasswordVisible) R.drawable.visibilty_off else R.drawable.visibility)
        passwordField.setSelection(passwordField.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        confirmPasswordField.inputType = if (isConfirmPasswordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        toggleConfirmPasswordButton.setImageResource(if (isConfirmPasswordVisible) R.drawable.visibilty_off else R.drawable.visibility)
        confirmPasswordField.setSelection(confirmPasswordField.text.length)
    }

    private fun handleRegister() {
        val password = passwordField.text.toString().trim()
        val confirmPassword = confirmPasswordField.text.toString().trim()

        if (email.isNullOrEmpty() || username.isNullOrEmpty() || fullName.isNullOrEmpty()) {
            showPopup("Data Tidak Lengkap", "Data pendaftaran tidak lengkap, silakan ulangi.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
            showPopup("Email Tidak Valid", "Format email tidak sesuai.")
            return
        }

        if (password.length < 6) {
            showPopup("Password Tidak Valid", "Password minimal 6 karakter.")
            return
        }

        if (password != confirmPassword) {
            showPopup("Konfirmasi Salah", "Konfirmasi password tidak cocok.")
            return
        }

        auth.fetchSignInMethodsForEmail(email!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        createAccount(email!!, password, username!!, fullName!!)
                    } else {
                        showPopup("Email Sudah Terdaftar", "Email sudah digunakan. Silakan login.") {
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }
                    }
                } else {
                    showPopup("Kesalahan", "Gagal mengecek email: ${task.exception?.message}")
                }
            }
    }

    private fun createAccount(email: String, password: String, username: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "uid" to userId,
                            "email" to email,
                            "username" to username,
                            "username_lowercase" to username.lowercase(),
                            "fullName" to fullName
                        )

                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                showPopup("Registrasi Berhasil", "Akun berhasil dibuat.") {
                                    startActivity(Intent(this, Login::class.java))
                                    finish()
                                }
                            }
                            .addOnFailureListener { e ->
                                showPopup("Gagal Simpan", "Gagal menyimpan data pengguna: ${e.message}")
                            }
                    } else {
                        showPopup("Kesalahan UID", "Gagal mendapatkan UID pengguna.")
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Terjadi kesalahan saat membuat akun."
                    if (errorMessage.contains("email address is already in use")) {
                        showPopup("Email Sudah Terdaftar", "Email sudah digunakan. Silakan login.") {
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }
                    } else {
                        showPopup("Gagal Registrasi", errorMessage)
                    }
                }
            }
    }
}
