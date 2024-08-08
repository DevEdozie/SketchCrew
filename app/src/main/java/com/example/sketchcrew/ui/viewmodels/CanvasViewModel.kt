package com.example.sketchcrew.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.repository.CanvasRepository

class CanvasViewModel(private val repository: CanvasRepository) : ViewModel() {

    private val _canvases = MutableLiveData<List<CanvasData>>()
    val canvases: LiveData<List<CanvasData>> get() = _canvases

    fun loadCanvases() {
        _canvases.value = repository.getAllCanvases()
    }

    fun saveCanvas(canvas: CanvasData) {
        repository.saveCanvas(canvas)
        loadCanvases()
    }

    fun getCanvas(id: Long): CanvasData? {
        return repository.loadCanvas(id)
    }

    fun deleteCanvas(canvas: CanvasData){
        repository.deleteCanvas(canvas)
    }
}
