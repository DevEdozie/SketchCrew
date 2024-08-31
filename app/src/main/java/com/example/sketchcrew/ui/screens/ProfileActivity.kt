package com.example.sketchcrew.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.sketchcrew.databinding.ActivityProfileBinding
import com.example.sketchcrew.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Launcher to get image from gallery
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

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        // Set up click listener for oldImageView to open the image picker
        binding.oldImageView.setOnClickListener {
            if (hasStoragePermission()) {
                openImagePicker()
            } else {
                requestStoragePermission()
            }
        }

        binding.button.setOnClickListener {
            logout()
        }

        // Load the saved image URI if available
        val savedImageUri = getSavedImageUri()
        if (savedImageUri != null) {
            loadImage(savedImageUri)
        } else {
            // Show oldImageView if no image is saved
            binding.profileImage.visibility = android.view.View.GONE
            binding.oldImageView.visibility = android.view.View.VISIBLE
        }

        // Display user's email
        displayUserEmail()
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

    private fun loadImage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            binding.profileImage.setImageBitmap(bitmap)
            binding.profileImage.visibility = android.view.View.VISIBLE
            binding.oldImageView.visibility = android.view.View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
           // Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadAndSaveImage(uri: Uri) {
        // Display image and save URI
        loadImage(uri)
        saveImageUriToPreferences(uri)
    }

    private fun saveImageUriToPreferences(uri: Uri) {
        val sharedPref = getSharedPreferences("profile_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("saved_image_uri", uri.toString())
            apply()
        }
    }

    private fun getSavedImageUri(): Uri? {
        val sharedPref = getSharedPreferences("profile_prefs", MODE_PRIVATE)
        val uriString = sharedPref.getString("saved_image_uri", null)
        return uriString?.let { Uri.parse(it) }
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
                Toast.makeText(this, "Permission denied. Unable to access storage.", Toast.LENGTH_SHORT).show()
                Log.d("ProfileActivity", "Permission denied: ${permissions[0]}")
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
    }
}
