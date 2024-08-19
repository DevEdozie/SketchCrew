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
import com.example.sketchcrew.ui.screens.HomeActivity
import com.example.sketchcrew.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Lazy initialization of the loading dialog in an Activity
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

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
//            binding.animationView.visibility = View.VISIBLE
            showLoading()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
//                        binding.animationView.visibility = View.INVISIBLE
                        stopLoading()
                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
//                        binding.animationView.visibility = View.INVISIBLE
                        stopLoading()
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
//                binding.animationView.visibility = View.INVISIBLE
                stopLoading()
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

    // Function to show the loading dialog
    private fun showLoading() {
        loadingDialog.show()
    }

    // Function to stop the loading dialog
    private fun stopLoading() {
        loadingDialog.cancel()
    }
}