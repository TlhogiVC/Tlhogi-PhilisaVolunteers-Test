package com.philisa.volunteers.data.model

import com.google.firebase.firestore.DocumentId

/**
 * A volunteer's sign-up for a specific [Activity] — distinct from [VolunteerApplication],
 * which is the initial "become a volunteer" application (Figs 51-54).
 */
data class ActivityApplication(
    @DocumentId
    val id: String = "",
    val activityId: String = "",
    val userId: String = "",
    val volunteerName: String = "",
    val status: String = STATUS_PENDING,
    val appliedDate: Long = 0L
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_CONFIRMED = "confirmed"
        const val STATUS_CANCELLED = "cancelled"
    }
}
