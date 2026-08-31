package com.philisa.volunteers.admin.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.Announcement
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.data.repository.AnnouncementRepository
import com.philisa.volunteers.data.repository.ActivityRepository
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminDashboardViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val activityRepository = ActivityRepository()
    private val announcementRepository = AnnouncementRepository()
    private val applicationRepository = ApplicationRepository()

    private val _pendingCount = MutableLiveData(0)
    val pendingCount: LiveData<Int> = _pendingCount

    private val _totalVolunteers = MutableLiveData(0)
    val totalVolunteers: LiveData<Int> = _totalVolunteers

    private val _publishedActivities = MutableLiveData(0)
    val publishedActivities: LiveData<Int> = _publishedActivities

    private val _activityDrafts = MutableLiveData(0)
    val activityDrafts: LiveData<Int> = _activityDrafts

    private val _announcementsCount = MutableLiveData(0)
    val announcementsCount: LiveData<Int> = _announcementsCount

    private val _announcementDrafts = MutableLiveData(0)
    val announcementDrafts: LiveData<Int> = _announcementDrafts

    private val _totalActivityApplications = MutableLiveData(0)
    val totalActivityApplications: LiveData<Int> = _totalActivityApplications

    private val _recentApplications = MutableLiveData<List<VolunteerApplication>>(emptyList())
    val recentApplications: LiveData<List<VolunteerApplication>> = _recentApplications

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadDashboard() {
        _isLoading.value = true
        viewModelScope.launch {
            val applications = applicationRepository.getApplicationsByStatus(null)
            _pendingCount.value = applications.count { it.status == VolunteerApplication.STATUS_PENDING }
            _recentApplications.value = applications.sortedByDescending { it.appliedDate }.take(4)

            _totalVolunteers.value = userRepository.getApprovedVolunteers().size

            val activities = activityRepository.getAllActivities()
            _publishedActivities.value = activities.count { it.status == Activity.STATUS_PUBLISHED }
            _activityDrafts.value = activities.count { it.status == Activity.STATUS_DRAFT }

            val announcements = announcementRepository.getAllAnnouncements()
            _announcementsCount.value = announcements.size
            _announcementDrafts.value = announcements.count { it.status == Announcement.STATUS_DRAFT }

            var totalApplications = 0
            activities.forEach { activity ->
                totalApplications += applicationRepository.getActivityApplicationsForActivity(activity.id).size
            }
            _totalActivityApplications.value = totalApplications

            _isLoading.value = false
        }
    }
}
