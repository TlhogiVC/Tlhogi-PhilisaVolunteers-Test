package com.philisa.volunteers.ui.volunteer.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.model.Announcement
import com.philisa.volunteers.data.model.GalleryItem
import com.philisa.volunteers.data.model.ImpactStats
import com.philisa.volunteers.data.repository.AnnouncementRepository
import com.philisa.volunteers.data.repository.CommunityRepository
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {

    private val announcementRepository = AnnouncementRepository()
    private val communityRepository = CommunityRepository()

    private val _impactStats = MutableLiveData<ImpactStats?>()
    val impactStats: LiveData<ImpactStats?> = _impactStats

    private val _announcements = MutableLiveData<List<Announcement>>(emptyList())
    val announcements: LiveData<List<Announcement>> = _announcements

    private val _galleryItems = MutableLiveData<List<GalleryItem>>(emptyList())
    val galleryItems: LiveData<List<GalleryItem>> = _galleryItems

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCommunity() {
        _isLoading.value = true
        viewModelScope.launch {
            _impactStats.value = communityRepository.getImpactStats()
            _announcements.value = announcementRepository.getPublishedAnnouncements()
            _galleryItems.value = communityRepository.getGalleryItems()
            _isLoading.value = false
        }
    }
}
