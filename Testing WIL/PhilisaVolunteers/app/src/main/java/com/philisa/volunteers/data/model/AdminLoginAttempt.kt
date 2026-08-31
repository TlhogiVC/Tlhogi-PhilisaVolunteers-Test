package com.philisa.volunteers.data.model

import com.google.firebase.firestore.DocumentId

/**
 * New model (approved in Stage 1 review) — logs every admin-tab sign-in attempt so the
 * "unauthorised login attempts are logged" notice on the Admin Login screen (Fig 67) is true.
 * Write-only: no screen in the PDF displays this log, so none is generated here.
 */
data class AdminLoginAttempt(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val success: Boolean = false,
    val timestamp: Long = 0L
)
