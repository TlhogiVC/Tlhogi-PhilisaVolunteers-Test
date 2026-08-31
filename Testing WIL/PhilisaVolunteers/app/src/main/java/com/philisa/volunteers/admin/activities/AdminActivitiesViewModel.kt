package com.philisa.volunteers.admin.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.repository.ActivityRepository
import kotlinx.coroutines.launch

class AdminActivitiesViewModel : ViewModel() {

    private val activityRepository = ActivityRepository()

    private val _activities = MutableLiveData<List<Activity>>(emptyList())
    val activities: LiveData<List<Activity>> = _activities

    private val _selectedActivity = MutableLiveData<Activity?>()
    val selectedActivity: LiveData<Activity?> = _selectedActivity

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveError = MutableLiveData<String?>()
    val saveError: LiveData<String?> = _saveError

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun loadActivities() {
        _isLoading.value = true
        viewModelScope.launch {
            _activities.value = activityRepository.getAllActivities().sortedByDescending { it.createdDate }
            _isLoading.value = false
        }
    }

    fun loadActivity(activityId: String) {
        val cached = _activities.value?.firstOrNull { it.id == activityId }
        if (cached != null) {
            _selectedActivity.value = cached
            return
        }
        viewModelScope.launch { _selectedActivity.value = activityRepository.getActivity(activityId) }
    }

    fun togglePublish(activity: Activity) {
        val newStatus = if (activity.status == Activity.STATUS_PUBLISHED) Activity.STATUS_DRAFT else Activity.STATUS_PUBLISHED
        viewModelScope.launch {
            activityRepository.setPublishStatus(activity.id, newStatus)
            loadActivities()
        }
    }

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            activityRepository.deleteActivity(activity.id)
            loadActivities()
        }
    }

    fun createActivity(activity: Activity) {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val result = activityRepository.createActivity(activity, uid)
            _isLoading.value = false
            result.onSuccess { _saveSuccess.value = true }.onFailure { _saveError.value = it.message }
        }
    }

    fun updateActivity(activity: Activity) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = activityRepository.updateActivity(activity)
            _isLoading.value = false
            result.onSuccess { _saveSuccess.value = true }.onFailure { _saveError.value = it.message }
        }
    }
}
