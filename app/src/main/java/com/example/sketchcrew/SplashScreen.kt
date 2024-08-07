package com.example.sketchcrew

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
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
                navigateToMainActivity()
            }, 4000)
        } else {
            // Delayed navigation to SignInActivity if no user is signed in
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToSignInActivity()
            }, 4000)  // 2 seconds delay
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Finish the SplashScreen activity so the user cannot go back to it
    }

    private fun navigateToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()  // Finish the SplashScreen activity so the user cannot go back to it
    }
}
