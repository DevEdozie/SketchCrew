package com.example.sketchcrew.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sketchcrew.data.local.dao.DrawingDao
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.data.local.models.Drawing
import com.example.sketchcrew.data.local.models.PathData
import com.example.sketchcrew.repository.CanvasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class CanvasViewModel(private val repository: CanvasRepository) : ViewModel() {

    private val _canvases = MutableLiveData<List<CanvasData>>()
    val canvases: LiveData<List<CanvasData>> get() = _canvases

    private val _drawings = MutableLiveData<List<Drawing>>()
    val drawing: LiveData<List<Drawing>> get() = _drawings

    val loadDrawings: LiveData<List<Drawing>> = repository.getAllDrawings.asLiveData()

    suspend fun saveDrawing(drawing:Drawing) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveDrawing(drawing)
        }
    }

    fun getDrawingById(id: Long): LiveData<Drawing> = repository.getDrawingById(id)

    suspend fun savePath(pathData: PathData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePath(pathData)
        }
    }

    suspend fun updatePath(pathData: PathData) {
        viewModelScope.launch {
            repository.updatePath(pathData)
        }
    }
    suspend fun getPath(pathId: Int): PathData {
        return repository.getPath(pathId)!!
    }

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
