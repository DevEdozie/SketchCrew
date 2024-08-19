package com.example.sketchcrew

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sketchcrew.databinding.ActivitySignUpBinding
import com.example.sketchcrew.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    // Lazy initialization of the loading dialog in an Activity
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize firebase auth
        firebaseAuth = Firebase.auth
        setUpSignUpButton()
        setUpNav()
    }

    private fun setUpSignUpButton() {
        binding.signUpButton.setOnClickListener {
//            binding.animationView.visibility = View.VISIBLE
            showLoading()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
//                            binding.animationView.visibility = View.INVISIBLE
                            stopLoading()
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        } else {
//                            binding.animationView.visibility = View.INVISIBLE
                            stopLoading()
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
//                    binding.animationView.visibility = View.INVISIBLE
                    stopLoading()
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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
            val intent = Intent(this, SignInActivity::class.java)
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