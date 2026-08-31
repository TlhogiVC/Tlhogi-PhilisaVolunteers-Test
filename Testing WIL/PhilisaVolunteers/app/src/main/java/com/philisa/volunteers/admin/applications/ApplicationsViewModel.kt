package com.philisa.volunteers.admin.applications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.repository.ActivityRepository
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.data.repository.UserRepository
import kotlinx.coroutines.launch

class ApplicationsViewModel : ViewModel() {

    private val activityRepository = ActivityRepository()
    private val applicationRepository = ApplicationRepository()
    private val userRepository = UserRepository()

    private val _activities = MutableLiveData<List<Activity>>(emptyList())
    val activities: LiveData<List<Activity>> = _activities

    private val _selectedActivity = MutableLiveData<Activity?>()
    val selectedActivity: LiveData<Activity?> = _selectedActivity

    private val _applicants = MutableLiveData<List<ActivityApplication>>(emptyList())
    val applicants: LiveData<List<ActivityApplication>> = _applicants

    private val _selectedApplicant = MutableLiveData<ActivityApplication?>()
    val selectedApplicant: LiveData<ActivityApplication?> = _selectedApplicant

    private val _selectedApplicantUser = MutableLiveData<User?>()
    val selectedApplicantUser: LiveData<User?> = _selectedApplicantUser

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadActivities() {
        _isLoading.value = true
        viewModelScope.launch {
            val all = activityRepository.getAllActivities()
            _activities.value = all
            if (_selectedActivity.value == null && all.isNotEmpty()) {
                selectActivity(all.first())
            } else {
                _isLoading.value = false
            }
        }
    }

    fun selectActivity(activity: Activity) {
        _selectedActivity.value = activity
        _isLoading.value = true
        viewModelScope.launch {
            _applicants.value = applicationRepository.getActivityApplicationsForActivity(activity.id)
                .filter { it.status != ActivityApplication.STATUS_CANCELLED }
            _isLoading.value = false
        }
    }

    fun selectApplicant(applicationId: String) {
        val application = _applicants.value?.firstOrNull { it.id == applicationId }
        _selectedApplicant.value = application
        if (application != null) {
            viewModelScope.launch { _selectedApplicantUser.value = userRepository.getUser(application.userId) }
        }
    }

    fun confirmApplicant(application: ActivityApplication) {
        viewModelScope.launch {
            applicationRepository.confirmActivityApplication(application)
            _selectedActivity.value?.let { selectActivity(it) }
        }
    }

    fun removeApplicant(application: ActivityApplication) {
        viewModelScope.launch {
            applicationRepository.removeActivityApplication(application)
            _selectedActivity.value?.let { selectActivity(it) }
        }
    }
}
