package com.philisa.volunteers.ui.volunteer.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.data.repository.UserRepository
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val applicationRepository = ApplicationRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _monthsActive = MutableLiveData(0)
    val monthsActive: LiveData<Int> = _monthsActive

    private val _activityCount = MutableLiveData(0)
    val activityCount: LiveData<Int> = _activityCount

    private val _upcomingCount = MutableLiveData(0)
    val upcomingCount: LiveData<Int> = _upcomingCount

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun loadProfile() {
        val uid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val user = userRepository.getUser(uid)
            _user.value = user
            _monthsActive.value = user?.let { DateUtils.monthsSince(it.joinedDate) } ?: 0

            val myApplications = applicationRepository.getMyActivityApplications(uid)
                .filter { it.status != ActivityApplication.STATUS_CANCELLED }
            _activityCount.value = myApplications.size
            _upcomingCount.value = myApplications.count { it.status == ActivityApplication.STATUS_CONFIRMED }
            _isLoading.value = false
        }
    }

    fun saveProfile(phone: String, area: String, skills: List<String>, availability: List<String>) {
        val current = _user.value ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val updated = current.copy(phone = phone, area = area, skills = skills, availability = availability)
            val result = userRepository.updateUser(updated)
            _isLoading.value = false
            result.onSuccess {
                _user.value = updated
                _saveSuccess.value = true
            }
        }
    }

    fun signOut() {
        FirebaseAuthManager.signOut()
    }
}
