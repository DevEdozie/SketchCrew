package com.example.sketchcrew

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.ui.screens.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {

            Handler(Looper.getMainLooper()).postDelayed({
                navigateToHomeActivity()
            }, 4000)
        } else {
            // Delayed navigation to SignInActivity if no user is signed in
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToWelcomeActivity()
            }, 4000)  // 2 seconds delay
        }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()  // Finish the SplashScreen activity so the user cannot go back to it
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()  // Finish the SplashScreen activity so the user cannot go back to it
    }
}
