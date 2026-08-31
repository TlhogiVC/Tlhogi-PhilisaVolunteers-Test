package com.philisa.volunteers.admin.impact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.model.ImpactStats
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.repository.CommunityRepository
import com.philisa.volunteers.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * New screen (Stage 1 decision) — the PDF has no admin UI for editing Home/Community's impact
 * counters or Volunteer of the Month, so this was added rather than seeding that data manually.
 */
class ImpactStatsAdminViewModel : ViewModel() {

    private val communityRepository = CommunityRepository()
    private val userRepository = UserRepository()

    private val _stats = MutableLiveData<ImpactStats>()
    val stats: LiveData<ImpactStats> = _stats

    private val _approvedVolunteers = MutableLiveData<List<User>>(emptyList())
    val approvedVolunteers: LiveData<List<User>> = _approvedVolunteers

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun loadStats() {
        _isLoading.value = true
        viewModelScope.launch {
            _stats.value = communityRepository.getImpactStats()
            _approvedVolunteers.value = userRepository.getApprovedVolunteers()
            _isLoading.value = false
        }
    }

    fun saveStats(stats: ImpactStats) {
        _isLoading.value = true
        viewModelScope.launch {
            communityRepository.updateImpactStats(stats)
            _isLoading.value = false
            _saveSuccess.value = true
        }
    }
}
