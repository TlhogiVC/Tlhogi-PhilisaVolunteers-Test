package com.philisa.volunteers.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val area: String = "",
    val role: String = ROLE_VOLUNTEER,
    val volunteerId: String = "",
    val skills: List<String> = emptyList(),
    val availability: List<String> = emptyList(),
    val joinedDate: Long = 0L
) {
    val fullName: String get() = "$firstName $lastName".trim()

    companion object {
        const val ROLE_VOLUNTEER = "volunteer"
        const val ROLE_ADMIN = "admin"
    }
}
