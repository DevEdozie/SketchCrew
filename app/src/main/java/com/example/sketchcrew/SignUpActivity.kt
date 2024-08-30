package com.example.sketchcrew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.databinding.ActivitySignUpBinding
import com.example.sketchcrew.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

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
        setUpTextWatchers()  // Set up TextWatchers for real-time validation
        setUpSignUpButton()
        setUpNav()
    }

    private fun setUpTextWatchers() {
        // Email validation
        binding.emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!email.contains("@")) {
                    binding.emailLayout.error = "Please enter a valid email address."
                } else {
                    val parts = email.split("@")
                    val userName = parts[0]
                    val restrictedCharsPattern = """[.$#\[\]]""".toRegex()
                    if (restrictedCharsPattern.containsMatchIn(userName)) {
                        binding.emailLayout.error =
                            "The part before '@' contains restricted characters: .\$#[]."
                    } else {
                        binding.emailLayout.error = null
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Password validation
        binding.passET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pass = s.toString()
                if (!isPasswordValid(pass)) {
                    binding.passwordLayout.error =
                        "Password must be 8-16 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character."
                } else {
                    binding.passwordLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Confirm Password validation
        binding.confirmPassEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pass = binding.passET.text.toString()
                val confirmPass = s.toString()
                if (pass != confirmPass) {
                    binding.confirmPasswordLayout.error = "Passwords do not match."
                } else {
                    binding.confirmPasswordLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setUpSignUpButton() {
        binding.signUpButton.setOnClickListener {
            showLoading()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            // Proceed only if there are no validation errors
            if (binding.emailLayout.error == null &&
                binding.passwordLayout.error == null &&
                binding.confirmPasswordLayout.error == null &&
                email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()
            ) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        stopLoading()
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, SignInActivity::class.java))
                        }
                    }
                    .addOnFailureListener { e ->
                        val errorMessage = when (e) {
                            is FirebaseAuthWeakPasswordException -> "The password is too weak."
                            is FirebaseAuthInvalidCredentialsException -> "The email address is badly formatted."
                            is FirebaseAuthUserCollisionException -> "This email address is already in use."
                            is FirebaseNetworkException -> "Network error. Please check your connection."
                            else -> "Registration failed: ${e.message}"
                        }
                        stopLoading()
                        Toast.makeText(
                            this,
                            "Registration failed: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                stopLoading()
                Toast.makeText(this, "Please correct the errors above.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern =
            """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,16}$""".toRegex()
        return passwordPattern.matches(password)
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
