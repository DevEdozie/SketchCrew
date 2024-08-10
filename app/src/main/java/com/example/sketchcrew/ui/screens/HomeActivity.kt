package com.example.sketchcrew.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.MainActivity
import com.example.sketchcrew.R
import com.example.sketchcrew.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.explore -> {
                    navigateTo(SketchesActivity::class.java)
                    true
                }
//                R.id.profile -> {
//                    navigateTo(ProfileActivity::class.java)
//                    true
//                }
                else -> false
            }
        }

        binding.fab.setOnClickListener{
            navigateTo(MainActivity::class.java)
        }
    }



    // Helper function to navigate to a different activity
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        // finish()
    }
}
