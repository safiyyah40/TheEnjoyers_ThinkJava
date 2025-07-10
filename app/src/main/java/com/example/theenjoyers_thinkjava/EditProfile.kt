package com.example.theenjoyers_thinkjava

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EditProfile : AppCompatActivity() {

    private lateinit var editTextNamaLengkap: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextAge: EditText
    private lateinit var btnAddPhoto: ImageButton
    private lateinit var btnSimpan: Button
    private lateinit var imageAvatar: ImageView
    private var selectedImageUri: Uri? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val REQUEST_STORAGE_PERMISSION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_edit_profile)

        // Inisialisasi view
        editTextNamaLengkap = findViewById(R.id.editTextNamaLengkap)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextAge = findViewById(R.id.editTextAge)
        btnAddPhoto = findViewById(R.id.btn_add_photo)
        btnSimpan = findViewById(R.id.Simpan)
        imageAvatar = findViewById(R.id.image_avatar)

        val userId = currentUser.uid

        // Ambil data dari Firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    editTextNamaLengkap.setText(document.getString("fullName") ?: "")
                    editTextUsername.setText(document.getString("username") ?: "")
                    editTextEmail.setText(document.getString("email") ?: "")
                    editTextAge.setText(document.getLong("age")?.toString() ?: "")

                    val photoUrl = document.getString("photoUrl")
                    Log.d("PhotoURL", "URL: $photoUrl")

                    Glide.with(this)
                        .load(photoUrl.ifNullOrEmpty { null })
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .fallback(R.drawable.ic_default_avatar)
                        .dontAnimate()
                        .signature(ObjectKey(System.currentTimeMillis()))
                        .circleCrop()
                        .into(imageAvatar)
                }
            }

        // Pilih foto dari galeri
        btnAddPhoto.setOnClickListener {
            checkStoragePermissionAndPickImage()
        }

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            val namaLengkap = editTextNamaLengkap.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val age = editTextAge.text.toString().trim().toIntOrNull()

            if (namaLengkap.isEmpty() || username.isEmpty() || email.isEmpty() || age == null) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userData = hashMapOf(
                "fullName" to namaLengkap,
                "username" to username,
                "email" to email,
                "age" to age
            )

            if (selectedImageUri != null) {
                lifecycleScope.launch {
                    val photoUrl = uploadToSupabase(selectedImageUri!!, userId)
                    if (photoUrl != null) {
                        userData["photoUrl"] = photoUrl

                        updateUserFirestore(userId, userData)

                        Toast.makeText(this@EditProfile, "Foto berhasil diunggah", Toast.LENGTH_SHORT).show()

                        Glide.with(this@EditProfile)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.ic_default_avatar)
                            .fallback(R.drawable.ic_default_avatar)
                            .dontAnimate()
                            .signature(ObjectKey(System.currentTimeMillis()))
                            .circleCrop()
                            .into(imageAvatar)
                    } else {
                        Toast.makeText(this@EditProfile, "Gagal upload foto ke Supabase", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                updateUserFirestore(userId, userData)
            }
        }
    }

    private fun updateUserFirestore(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update profil", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            imageAvatar.setImageURI(selectedImageUri)
        }
    }

    private fun checkStoragePermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_STORAGE_PERMISSION)
            } else {
                openImagePicker()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
            } else {
                openImagePicker()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Izin dibutuhkan untuk memilih foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Extension function tambahan
    private fun String?.ifNullOrEmpty(default: () -> String?): String? {
        return if (this.isNullOrEmpty()) default() else this
    }

    private suspend fun uploadToSupabase(uri: Uri, userId: String): String? {
        val supabaseUrl = "https://uoutenfhjppsbigvrgwi.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVvdXRlbmZoanBwc2JpZ3ZyZ3dpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTIxMzk4NzMsImV4cCI6MjA2NzcxNTg3M30.avbSfwfpCNCrzyu8Q3nlQh3mFgDxBBD0yaVXrt6i8D8"
        val filePath = "$userId/avatar.jpg"
        val mimeType = "image/jpeg"

        val fileBytes = withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.readBytes()
        } ?: return null

        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val response: HttpResponse = client.put("$supabaseUrl/storage/v1/object/profile-pictures/$filePath") {
            header("Authorization", "Bearer $supabaseKey")
            header("Content-Type", mimeType)
            setBody(fileBytes)
        }

        val responseBody = response.bodyAsText()
        Log.e("SupabaseUpload", "Status: ${response.status}, Body: $responseBody")

        client.close()

        return if (response.status.value in 200..299) {
            "$supabaseUrl/storage/v1/object/public/profile-pictures/$filePath"
        } else {
            null
        }
    }
}
