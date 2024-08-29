package com.example.sketchcrew.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sketchcrew.databinding.ActivityProfileBinding
import com.example.sketchcrew.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val imagePickerLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadAndSaveImage(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        binding.oldImageView.setOnClickListener {
            if (hasStoragePermission()) {
                openImagePicker()
            } else {
                requestStoragePermission()
            }
        }

        binding.profileImage.setOnClickListener {
            if (hasStoragePermission()) {
                openImagePicker()
            } else {
                requestStoragePermission()
            }
        }

        binding.button.setOnClickListener {
            logout()
        }

        displayUserEmail()
        loadUserProfileImage() // Load the user's profile image
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun loadUserProfileImage() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.contains("profileImageUrl")) {
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        // Use Glide to load the image
                        Glide.with(this)
                            .load(profileImageUrl)
                            .into(binding.profileImage)
                        binding.profileImage.visibility = android.view.View.VISIBLE
                        binding.oldImageView.visibility = android.view.View.GONE
                    } else {
                        binding.profileImage.visibility = android.view.View.GONE
                        binding.oldImageView.visibility = android.view.View.VISIBLE
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("ProfileActivity", "Failed to load profile image URL: ${e.message}")
                Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadAndSaveImage(uri: Uri) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val storageRef = storage.reference.child("images/${user.uid}/${UUID.randomUUID()}.jpg")
            val uploadTask = storageRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveImageUriToDatabase(downloadUri.toString())
                    loadUserProfileImage()
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageUriToDatabase(downloadUri: String) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)
            userRef.update("profileImageUrl", downloadUri)
                .addOnSuccessListener {
                    Log.d("ProfileActivity", "Image URL saved to database successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileActivity", "Failed to save image URL: ${e.message}")
                }
        }
    }

    private fun displayUserEmail() {
        val user = firebaseAuth.currentUser
        binding.emailTexView.text = user?.email ?: "Email not available"
    }

    private fun logout() {
        firebaseAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Unable to access storage.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("ProfileActivity", "Permission denied: ${permissions[0]}")
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
    }
}
