package com.example.sketchcrew.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.repository.CanvasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class CanvasViewModel(private val repository: CanvasRepository) : ViewModel() {

    private val _canvases = MutableLiveData<List<CanvasData>>()
    val canvases: LiveData<List<CanvasData>> get() = _canvases

    suspend fun loadCanvases() {
        _canvases.postValue( repository.getAllCanvases())
    }

    suspend fun saveCanvas(canvas: CanvasData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveCanvas(canvas)
        }
    }

    suspend fun getCanvas(id: Long): CanvasData? {
        return repository.loadCanvas(id)
    }

    suspend fun deleteCanvas(canvas: CanvasData){
        repository.deleteCanvas(canvas)
    }
}
