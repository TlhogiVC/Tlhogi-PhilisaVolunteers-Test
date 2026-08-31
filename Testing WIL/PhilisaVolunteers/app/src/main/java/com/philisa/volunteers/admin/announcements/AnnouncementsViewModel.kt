package com.philisa.volunteers.admin.announcements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.Announcement
import com.philisa.volunteers.data.repository.AnnouncementRepository
import kotlinx.coroutines.launch

class AnnouncementsViewModel : ViewModel() {

    private val announcementRepository = AnnouncementRepository()

    private val _announcements = MutableLiveData<List<Announcement>>(emptyList())
    val announcements: LiveData<List<Announcement>> = _announcements

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveError = MutableLiveData<String?>()
    val saveError: LiveData<String?> = _saveError

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun loadAnnouncements() {
        _isLoading.value = true
        viewModelScope.launch {
            _announcements.value = announcementRepository.getAllAnnouncements()
            _isLoading.value = false
        }
    }

    fun togglePublish(announcement: Announcement) {
        val newStatus = if (announcement.status == Announcement.STATUS_PUBLISHED) Announcement.STATUS_DRAFT else Announcement.STATUS_PUBLISHED
        viewModelScope.launch {
            announcementRepository.setPublishStatus(announcement.id, newStatus)
            loadAnnouncements()
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            announcementRepository.deleteAnnouncement(announcement.id)
            loadAnnouncements()
        }
    }

    fun createAnnouncement(title: String, body: String, publish: Boolean) {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val announcement = Announcement(
                title = title,
                messageBody = body,
                status = if (publish) Announcement.STATUS_PUBLISHED else Announcement.STATUS_DRAFT
            )
            val result = announcementRepository.createAnnouncement(announcement, uid)
            _isLoading.value = false
            result.onSuccess { _saveSuccess.value = true }.onFailure { _saveError.value = it.message }
        }
    }
}
