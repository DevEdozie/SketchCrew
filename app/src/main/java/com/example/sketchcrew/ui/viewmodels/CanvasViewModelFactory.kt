package com.example.sketchcrew.ui.viewmodels

import android.content.Context
import com.example.sketchcrew.repository.CanvasRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CanvasViewModelFactory(
    private val repository: CanvasRepository
    ) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CanvasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CanvasViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}