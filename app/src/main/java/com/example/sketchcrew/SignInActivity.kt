package com.example.sketchcrew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sketchcrew.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        // Initialize firebase auth
        firebaseAuth = Firebase.auth
        setContentView(binding.root)
        setUpNav()
        setUpSignInButton()

    }

    private fun setUpSignInButton() {
        binding.signInBtn.setOnClickListener {
            binding.animationView.visibility = View.VISIBLE
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.animationView.visibility = View.INVISIBLE
                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        binding.animationView.visibility = View.INVISIBLE
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.animationView.visibility = View.INVISIBLE
                Toast.makeText(this, "Fill up empty fields", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setUpNav() {
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}