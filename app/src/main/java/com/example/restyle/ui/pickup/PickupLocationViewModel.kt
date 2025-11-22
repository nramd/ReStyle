package com.example.restyle.ui.pickup

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restyle.data.model.Photo
import com.example.restyle.data.model.PhotoUploadState
import com.example.restyle.data.repository.PhotoRepository
import com.example.restyle.data.repository.StorageRepository
import com.example.restyle.data.repository.UploadResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class PickupLocationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "PickupLocationVM"
    }

    private val photoRepository = PhotoRepository(application.applicationContext)
    private val storageRepository = StorageRepository(application.applicationContext)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uploadState = MutableStateFlow(PhotoUploadState())
    val uploadState: StateFlow<PhotoUploadState> = _uploadState.asStateFlow()

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location.asStateFlow()

    private val _address = MutableStateFlow("No location selected")
    val address: StateFlow<String> = _address.asStateFlow()

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Getting current location...")

                val cancellationTokenSource = CancellationTokenSource()

                val locationResult = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()

                if (locationResult != null) {
                    Log.d(TAG, "Location found: ${locationResult.latitude}, ${locationResult.longitude}")

                    _location.value = LocationData(
                        latitude = locationResult.latitude,
                        longitude = locationResult.longitude
                    )

                    // Get address from coordinates
                    getAddressFromLocation(context, locationResult.latitude, locationResult.longitude)
                } else {
                    Log.e(TAG, "Location is null")
                    _address.value = "Unable to get location"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting location", e)
                _address.value = "Error: ${e.message}"
            }
        }
    }

    private fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val addressText = buildString {
                        // Get detailed address
                        if (!address.thoroughfare.isNullOrEmpty()) {
                            append(address.thoroughfare) // Street name
                        }
                        if (!address.subLocality.isNullOrEmpty()) {
                            if (isNotEmpty()) append(", ")
                            append(address.subLocality) // District
                        }
                        if (!address.locality.isNullOrEmpty()) {
                            if (isNotEmpty()) append(", ")
                            append(address.locality) // City
                        }
                        if (!address.adminArea.isNullOrEmpty()) {
                            if (isNotEmpty()) append(", ")
                            append(address.adminArea) // Province
                        }
                        if (!address.postalCode.isNullOrEmpty()) {
                            if (isNotEmpty()) append(" ")
                            append(address.postalCode) // Postal code
                        }
                    }

                    _address.value = addressText.ifEmpty {
                        "Lat: ${String.format("%.6f", latitude)}, Lng: ${String.format("%.6f", longitude)}"
                    }

                    Log.d(TAG, "Address: $addressText")
                } else {
                    _address.value = "Lat: ${String.format("%.6f", latitude)}, Lng: ${String.format("%.6f", longitude)}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting address", e)
                _address.value = "Lat: ${String.format("%.6f", latitude)}, Lng: ${String.format("%.6f", longitude)}"
            }
        }
    }

    fun savePhotoWithLocation(
        imageUri: Uri,
        title: String,
        description: String,
        category: String,
        userId: String = "default_user"
    ) {
        val currentLocation = _location.value
        if (currentLocation == null) {
            _uploadState.value = PhotoUploadState(
                isLoading = false,
                error = "Please get your location first"
            )
            return
        }

        Log.d(TAG, "Saving photo with location: $currentLocation")

        viewModelScope.launch {
            _uploadState.value = PhotoUploadState(isLoading = true, uploadProgress = 0)

            try {
                // Upload foto ke Storage
                storageRepository.uploadPhoto(imageUri, userId, category).collect { result ->
                    when (result) {
                        is UploadResult.Progress -> {
                            Log.d(TAG, "Upload progress: ${result.progress}%")
                            _uploadState.value = PhotoUploadState(
                                isLoading = true,
                                uploadProgress = result.progress
                            )
                        }

                        is UploadResult.Success -> {
                            Log.d(TAG, "Upload success! Saving to Firestore...")

                            // Simpan metadata ke Firestore dengan lokasi
                            val photo = Photo(
                                imageUrl = result.downloadUrl,
                                title = title,
                                description = description,
                                category = category,
                                pickupLocation = _address.value,
                                pickupLat = currentLocation.latitude,
                                pickupLng = currentLocation.longitude,
                                userId = userId
                            )

                            photoRepository.savePhoto(photo).fold(
                                onSuccess = { photoId ->
                                    Log.d(TAG, "Firestore save success! ID: $photoId")
                                    _uploadState.value = PhotoUploadState(
                                        isLoading = false,
                                        isSuccess = true,
                                        uploadProgress = 100
                                    )
                                },
                                onFailure = { exception ->
                                    Log.e(TAG, "Firestore save failed", exception)
                                    _uploadState.value = PhotoUploadState(
                                        isLoading = false,
                                        error = "Failed to save: ${exception.message}",
                                        uploadProgress = 0
                                    )
                                }
                            )
                        }

                        is UploadResult.Error -> {
                            Log.e(TAG, "Upload error: ${result.message}")
                            _uploadState.value = PhotoUploadState(
                                isLoading = false,
                                error = result.message,
                                uploadProgress = 0
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during save", e)
                _uploadState.value = PhotoUploadState(
                    isLoading = false,
                    error = "Error: ${e.message}",
                    uploadProgress = 0
                )
            }
        }
    }

    fun resetState() {
        _uploadState.value = PhotoUploadState()
        _location.value = null
        _address.value = "No location selected"
    }
}

data class LocationData(
    val latitude: Double,
    val longitude: Double
)