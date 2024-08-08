package com.example.sketchcrew

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sketchcrew.databinding.FragmentThirdObScreenBinding


class ThirdObScreen : Fragment() {


    private lateinit var binding: FragmentThirdObScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdObScreenBinding.inflate(layoutInflater, container, false)

        binding.getStartedBtn.setOnClickListener {
        // Navigate to SignUpActivity
            val intent = Intent(activity, SignUpActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}