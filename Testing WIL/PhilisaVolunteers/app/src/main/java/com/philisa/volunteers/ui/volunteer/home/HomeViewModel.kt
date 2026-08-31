package com.philisa.volunteers.ui.volunteer.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.data.model.ImpactStats
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.repository.ActivityRepository
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.data.repository.CommunityRepository
import com.philisa.volunteers.data.repository.UserRepository
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val applicationRepository = ApplicationRepository()
    private val activityRepository = ActivityRepository()
    private val communityRepository = CommunityRepository()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _todaysActivities = MutableLiveData<List<Activity>>(emptyList())
    val todaysActivities: LiveData<List<Activity>> = _todaysActivities

    private val _comingUpActivities = MutableLiveData<List<Activity>>(emptyList())
    val comingUpActivities: LiveData<List<Activity>> = _comingUpActivities

    private val _assignedCount = MutableLiveData(0)
    val assignedCount: LiveData<Int> = _assignedCount

    private val _todayCount = MutableLiveData(0)
    val todayCount: LiveData<Int> = _todayCount

    private val _completedCount = MutableLiveData(0)
    val completedCount: LiveData<Int> = _completedCount

    private val _impactStats = MutableLiveData<ImpactStats?>()
    val impactStats: LiveData<ImpactStats?> = _impactStats

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadDashboard() {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _currentUser.value = userRepository.getUser(uid)

                val myApplications = applicationRepository.getMyActivityApplications(uid)
                    .filter { it.status != ActivityApplication.STATUS_CANCELLED }
                val activities = myApplications.mapNotNull { activityRepository.getActivity(it.activityId) }

                val now = DateUtils.now()
                val startOfToday = startOfDay(now)
                val endOfToday = startOfToday + DAY_MILLIS

                _todaysActivities.value = activities.filter { it.dateMillis in startOfToday until endOfToday }
                _comingUpActivities.value = activities.filter { it.dateMillis >= endOfToday }.sortedBy { it.dateMillis }
                _assignedCount.value = activities.size
                _todayCount.value = _todaysActivities.value?.size ?: 0
                _completedCount.value = activities.count { it.dateMillis < startOfToday }

                _impactStats.value = communityRepository.getImpactStats()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
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
