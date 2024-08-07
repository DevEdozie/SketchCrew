package com.example.sketchcrew.ui.screens


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set up the button click listeners
        binding.profileImage.setOnClickListener {
            // Handle profile image click
        }

        binding.fab.setOnClickListener {
            // Handle FloatingActionButton click
        }

        binding.homeTab.setOnClickListener {
            // Handle home tab click
        }

        binding.exploreTab.setOnClickListener {
            // Handle explore tab click
        }

        binding.profileTab.setOnClickListener {
            // Handle profile tab click
        }

        // Set up other UI elements or functionality here
    }
}
