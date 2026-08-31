package com.philisa.volunteers.ui.volunteer.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.data.repository.ActivityRepository
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleViewModel : ViewModel() {

    private val applicationRepository = ApplicationRepository()
    private val activityRepository = ActivityRepository()

    private val _todaysActivities = MutableLiveData<List<Activity>>(emptyList())
    val todaysActivities: LiveData<List<Activity>> = _todaysActivities

    private val _thisWeekActivities = MutableLiveData<List<Activity>>(emptyList())
    val thisWeekActivities: LiveData<List<Activity>> = _thisWeekActivities

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadSchedule() {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val myApplications = applicationRepository.getMyActivityApplications(uid)
                .filter { it.status != ActivityApplication.STATUS_CANCELLED }
            val activities = myApplications.mapNotNull { activityRepository.getActivity(it.activityId) }
                .sortedBy { it.dateMillis }

            val now = DateUtils.now()
            val startOfToday = startOfDay(now)
            val endOfToday = startOfToday + DAY_MILLIS
            val endOfWeek = startOfToday + 7 * DAY_MILLIS

            _todaysActivities.value = activities.filter { it.dateMillis in startOfToday until endOfToday }
            _thisWeekActivities.value = activities.filter { it.dateMillis >= startOfToday && it.dateMillis < endOfWeek }
            _isLoading.value = false
        }
    }

    private fun startOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}
