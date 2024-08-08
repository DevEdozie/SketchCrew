package com.example.sketchcrew.ui.screens


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sketchcrew.R
import com.example.sketchcrew.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bottomTab.addOnTabSelectedListener(this@HomeActivity)


    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> {

            }

            1 -> {
                //   startActivity(Intent(this@HomeActivity, SketchesActivity::class.java))
                val intent = Intent(applicationContext, SketchesActivity::class.java)
                startActivity(intent)
            }

            2 -> {
                // startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                val intent = Intent(applicationContext, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }


}
