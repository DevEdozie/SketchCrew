package com.example.sketchcrew

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sketchcrew.databinding.ActivitySignInBinding
import com.example.sketchcrew.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        val fragmentList = arrayListOf(
            FirstObScreen(),
            SecondObScreen(),
            ThirdObScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)
    }
}