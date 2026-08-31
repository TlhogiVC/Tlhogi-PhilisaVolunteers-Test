package com.philisa.volunteers.ui.volunteer.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.repository.ActivityRepository
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.data.repository.UserRepository
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.launch

enum class ActivityTimeFilter { TODAY, UPCOMING, COMPLETED }

class ActivitiesViewModel : ViewModel() {

    private val activityRepository = ActivityRepository()
    private val applicationRepository = ApplicationRepository()
    private val userRepository = UserRepository()

    private val _publishedActivities = MutableLiveData<List<ActivityListItem>>(emptyList())
    val publishedActivities: LiveData<List<ActivityListItem>> = _publishedActivities

    private val _myActivities = MutableLiveData<List<ActivityListItem>>(emptyList())
    val myActivities: LiveData<List<ActivityListItem>> = _myActivities

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentUser: User? = null
    private var myApplicationsByActivityId: Map<String, ActivityApplication> = emptyMap()

    private val _currentActivity = MutableLiveData<Activity?>()
    val currentActivity: LiveData<Activity?> = _currentActivity

    private val _currentApplication = MutableLiveData<ActivityApplication?>()
    val currentApplication: LiveData<ActivityApplication?> = _currentApplication

    private val _actionError = MutableLiveData<String?>()
    val actionError: LiveData<String?> = _actionError

    private val _actionSuccess = MutableLiveData(false)
    val actionSuccess: LiveData<Boolean> = _actionSuccess

    fun loadActivityDetails(activityId: String) {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            currentUser = userRepository.getUser(uid)
            _currentActivity.value = activityRepository.getActivity(activityId)
            _currentApplication.value = applicationRepository.getMyActivityApplication(activityId, uid)
            _isLoading.value = false
        }
    }

    fun applyForActivity() {
        val activity = _currentActivity.value ?: return
        val user = currentUser ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val result = applicationRepository.applyForActivity(activity, user)
            _isLoading.value = false
            result.onSuccess {
                _actionSuccess.value = true
                loadActivityDetails(activity.id)
            }.onFailure { _actionError.value = it.message }
        }
    }

    fun cancelParticipation() {
        val application = _currentApplication.value ?: return
        val activityId = application.activityId
        _isLoading.value = true
        viewModelScope.launch {
            val result = applicationRepository.cancelActivityApplication(application)
            _isLoading.value = false
            result.onSuccess {
                _actionSuccess.value = true
                loadActivityDetails(activityId)
            }.onFailure { _actionError.value = it.message }
        }
    }

    fun loadAvailableActivities() {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            currentUser = userRepository.getUser(uid)
            val myApplications = applicationRepository.getMyActivityApplications(uid)
                .filter { it.status != ActivityApplication.STATUS_CANCELLED }
            myApplicationsByActivityId = myApplications.associateBy { it.activityId }

            val published = activityRepository.getPublishedActivities()
                .filter { !myApplicationsByActivityId.containsKey(it.id) }
            _publishedActivities.value = published.map { ActivityListItem(it) }
            _isLoading.value = false
        }
    }

    fun loadMyActivities(filter: ActivityTimeFilter) {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val myApplications = applicationRepository.getMyActivityApplications(uid)
                .filter { it.status != ActivityApplication.STATUS_CANCELLED }
            myApplicationsByActivityId = myApplications.associateBy { it.activityId }

            val activities = myApplications.mapNotNull { app ->
                activityRepository.getActivity(app.activityId)?.let { it to app }
            }

            val now = DateUtils.now()
            val startOfToday = startOfDay(now)
            val endOfToday = startOfToday + DAY_MILLIS

            val filtered = activities.filter { (activity, _) ->
                when (filter) {
                    ActivityTimeFilter.TODAY -> activity.dateMillis in startOfToday until endOfToday
                    ActivityTimeFilter.UPCOMING -> activity.dateMillis >= endOfToday
                    ActivityTimeFilter.COMPLETED -> activity.dateMillis < startOfToday
                }
            }.sortedBy { it.first.dateMillis }

            _myActivities.value = filtered.map { (activity, app) -> ActivityListItem(activity, app.status) }
            _isLoading.value = false
        }
    }

    private fun startOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}
