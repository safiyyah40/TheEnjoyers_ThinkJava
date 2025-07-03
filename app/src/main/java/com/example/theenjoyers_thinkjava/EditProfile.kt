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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

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
    private val storage = FirebaseStorage.getInstance()

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
                    if (!photoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(photoUrl)
                            .into(imageAvatar)
                    }
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
                val fileRef = storage.reference.child("profile_pictures/$userId.jpg")
                fileRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            userData["photoUrl"] = uri.toString()
                            Glide.with(this)
                                .load(uri)
                                .into(imageAvatar)
                            updateUserFirestore(userId, userData)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal upload foto: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("UploadError", "Foto gagal diupload", e)
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
}
