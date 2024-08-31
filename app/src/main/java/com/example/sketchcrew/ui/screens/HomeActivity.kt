package com.example.sketchcrew.ui.screens


import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.MainActivity
import com.example.sketchcrew.R
import com.example.sketchcrew.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserProfileImage()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }
//                R.id.explore -> {
//                    navigateTo(SketchesActivity::class.java)
//                    true
//                }
                R.id.profile -> {
                    navigateTo(ProfileActivity::class.java)
                    true
                }

                else -> false
            }
        }

        binding.fab.setOnClickListener {
            navigateTo(MainActivity::class.java)
        }
    }


    private fun loadUserProfileImage() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Use the correct file path where the image is actually stored
            val storageRef = storage.reference.child("profile_images/${user.uid}/profile.jpg")

            // Create a temporary file for the image
            val localFile = File.createTempFile(user.uid, ".jpg")

            // Download the image file to the temporary file
            storageRef.getFile(localFile).addOnSuccessListener {
                // Decode the downloaded image file into a Bitmap
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.profileImage.setImageBitmap(bitmap)
                binding.profileImage.visibility = android.view.View.VISIBLE
                binding.oldImage.visibility = android.view.View.GONE
                binding.oldImage.visibility = android.view.View.GONE

            }.addOnFailureListener { exception ->
                // Handle any errors that occurred while getting the download URL
                Log.e(
                    "ProfileActivity",
                    "Failed to retrieve profile image from Storage: ${exception.message}"
                )
                Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
                binding.profileImage.visibility = android.view.View.GONE
                binding.oldImage.visibility = android.view.View.VISIBLE
            }
        }
    }

    // Helper function to navigate to a different activity
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        // finish()
    }
}
