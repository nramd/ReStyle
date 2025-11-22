package com.example.restyle.ui.photodetail

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restyle.data.model.Photo
import com.example.restyle.data.model.PhotoUploadState
import com.example.restyle.data.repository.PhotoRepository
import com.example.restyle.data.repository.StorageRepository
import com.example.restyle.data.repository.UploadResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val photoRepository = PhotoRepository(application.applicationContext)
    private val storageRepository = StorageRepository(application.applicationContext)
    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    private val _uploadState = MutableStateFlow(PhotoUploadState())
    val uploadState: StateFlow<PhotoUploadState> = _uploadState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    fun onPriceChange(newPrice: String) {
        if (newPrice.isEmpty() || newPrice.all { it.isDigit() }) {
            _price.value = newPrice
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun savePhoto(
        imageUri: Uri,
        category: String = "Resell",
        userId: String = "default_user"
    ) {
        viewModelScope.launch {
            _uploadState.value = PhotoUploadState(isLoading = true, uploadProgress = 0)

            // Upload foto ke Storage
            storageRepository.uploadPhoto(imageUri, userId, category).collect { result ->
                when (result) {
                    is UploadResult.Progress -> {
                        // Update progress
                        _uploadState.value = PhotoUploadState(
                            isLoading = true,
                            uploadProgress = result.progress
                        )
                    }

                    is UploadResult.Success -> {
                        // Foto berhasil diupload, simpan metadata ke Firestore
                        val photo = Photo(
                            imageUrl = result.downloadUrl,
                            title = _title.value,
                            description = _description.value,
                            category = category,
                            price = if (category == "Resell") _price.value.toLongOrNull() ?: 0 else 0,
                            userId = userId
                        )

                        photoRepository.savePhoto(photo).fold(
                            onSuccess = { photoId ->
                                _uploadState.value = PhotoUploadState(
                                    isLoading = false,
                                    isSuccess = true,
                                    uploadProgress = 100
                                )
                            },
                            onFailure = { exception ->
                                _uploadState.value = PhotoUploadState(
                                    isLoading = false,
                                    error = "Gagal menyimpan metadata: ${exception.message}",
                                    uploadProgress = 0
                                )
                            }
                        )
                    }

                    is UploadResult.Error -> {
                        _uploadState.value = PhotoUploadState(
                            isLoading = false,
                            error = result.message,
                            uploadProgress = 0
                        )
                    }
                }
            }
        }
    }

    fun resetState() {
        _uploadState.value = PhotoUploadState()
        _title.value = ""
        _description.value = ""
        _price.value = ""
    }
}