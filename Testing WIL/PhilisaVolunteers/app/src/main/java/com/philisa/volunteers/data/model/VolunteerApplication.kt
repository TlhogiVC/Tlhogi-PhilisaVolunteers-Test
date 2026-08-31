package com.philisa.volunteers.data.model

import com.google.firebase.firestore.DocumentId

data class VolunteerApplication(
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val area: String = "",
    val programmeInterest: String = "",
    val motivation: String = "",
    val status: String = STATUS_PENDING,
    val referenceNumber: String = "",
    val appliedDate: Long = 0L,
    val reviewedDate: Long = 0L,
    val reviewedBy: String = ""
) {
    val fullName: String get() = "$firstName $lastName".trim()

    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"
        const val STATUS_REJECTED = "rejected"
    }
}
