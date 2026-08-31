package com.philisa.volunteers.admin.gallery

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.model.GalleryItem
import com.philisa.volunteers.data.repository.CommunityRepository
import kotlinx.coroutines.launch

/** New screen (Stage 1 decision) — the PDF has no admin UI for adding/removing Community
 *  gallery photos (Fig 64), so this was added rather than seeding that data manually. */
class GalleryViewModel : ViewModel() {

    private val communityRepository = CommunityRepository()

    private val _items = MutableLiveData<List<GalleryItem>>(emptyList())
    val items: LiveData<List<GalleryItem>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadGallery() {
        _isLoading.value = true
        viewModelScope.launch {
            _items.value = communityRepository.getGalleryItems()
            _isLoading.value = false
        }
    }

    fun uploadPhoto(uri: Uri, caption: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = communityRepository.addGalleryItem(uri, caption)
            _isLoading.value = false
            result.onSuccess { loadGallery() }.onFailure { _errorMessage.value = it.message }
        }
    }

    fun deletePhoto(item: GalleryItem) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = communityRepository.deleteGalleryItem(item)
            _isLoading.value = false
            result.onSuccess { loadGallery() }.onFailure { _errorMessage.value = it.message }
        }
    }
}
