package com.example.restyle.ui.marketplace

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

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MarketplaceViewModel"
    }

    private val photoRepository = PhotoRepository(application.applicationContext)

    private val _marketplaceItems = MutableStateFlow<List<Photo>>(emptyList())
    val marketplaceItems: StateFlow<List<Photo>> = _marketplaceItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMarketplaceItems()
    }

    private fun loadMarketplaceItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                photoRepository.getPhotosByCategory("Resell").collect { photos ->
                    // Show all resell items (from all users)
                    _marketplaceItems.value = photos
                    _isLoading.value = false
                    Log.d(TAG, "Loaded ${photos.size} marketplace items")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading marketplace items", e)
                _isLoading.value = false
            }
        }
    }

    fun refreshItems() {
        loadMarketplaceItems()
    }
}