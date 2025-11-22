package com.example.restyle.ui.screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restyle.data.model.Photo
import com.example.restyle.data.repository.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val photoRepository = PhotoRepository(application.applicationContext)

    private val _myResellItems = MutableStateFlow<List<Photo>>(emptyList())
    val myResellItems: StateFlow<List<Photo>> = _myResellItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMyResellItems()
    }

    private fun loadMyResellItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                photoRepository.getPhotosByCategory("Resell").collect { photos ->
                    // Filter by current user (nanti akan pakai userId dari auth)
                    val myPhotos = photos.filter { it.userId == "default_user" }
                    _myResellItems.value = myPhotos
                    _isLoading.value = false
                    Log.d(TAG, "Loaded ${myPhotos.size} resell items")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading resell items", e)
                _isLoading.value = false
            }
        }
    }

    fun refreshItems() {
        loadMyResellItems()
    }
}