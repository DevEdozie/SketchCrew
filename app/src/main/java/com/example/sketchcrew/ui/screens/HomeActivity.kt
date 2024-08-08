package com.example.sketchcrew.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the TabLayout listener
        binding.bottomTab.addOnTabSelectedListener(this)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> Unit // If there's nothing to do on this tab, you can leave it empty
            1 -> navigateTo(SketchesActivity::class.java)
            2 -> navigateTo(ProfileActivity::class.java)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // Handle tab unselected state if needed
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // Handle tab reselected state if needed
    }

    // Helper function to navigate to a different activity
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        // Optionally finish the current activity
        // finish()
    }
}
