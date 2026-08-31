package com.philisa.volunteers.admin.volunteers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.data.repository.ApplicationRepository
import kotlinx.coroutines.launch

class VolunteersViewModel : ViewModel() {

    private val applicationRepository = ApplicationRepository()

    private val _allApplications = MutableLiveData<List<VolunteerApplication>>(emptyList())
    val allApplications: LiveData<List<VolunteerApplication>> = _allApplications

    private val _selectedApplication = MutableLiveData<VolunteerApplication?>()
    val selectedApplication: LiveData<VolunteerApplication?> = _selectedApplication

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _actionError = MutableLiveData<String?>()
    val actionError: LiveData<String?> = _actionError

    private val _actionSuccess = MutableLiveData(false)
    val actionSuccess: LiveData<Boolean> = _actionSuccess

    fun loadApplications() {
        _isLoading.value = true
        viewModelScope.launch {
            _allApplications.value = applicationRepository.getApplicationsByStatus(null)
                .sortedByDescending { it.appliedDate }
            _isLoading.value = false
        }
    }

    fun selectApplication(applicationId: String) {
        val cached = _allApplications.value?.firstOrNull { it.id == applicationId }
        if (cached != null) {
            _selectedApplication.value = cached
            return
        }
        viewModelScope.launch {
            _selectedApplication.value = applicationRepository.getApplication(applicationId)
        }
    }

    fun approve(application: VolunteerApplication) {
        val reviewerUid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val result = applicationRepository.approveApplication(application, reviewerUid)
            _isLoading.value = false
            result.onSuccess {
                _actionSuccess.value = true
                loadApplications()
            }.onFailure { _actionError.value = it.message }
        }
    }

    fun reject(application: VolunteerApplication) {
        val reviewerUid = FirebaseAuthManager.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val result = applicationRepository.rejectApplication(application.id, reviewerUid)
            _isLoading.value = false
            result.onSuccess {
                _actionSuccess.value = true
                loadApplications()
            }.onFailure { _actionError.value = it.message }
        }
    }
}
