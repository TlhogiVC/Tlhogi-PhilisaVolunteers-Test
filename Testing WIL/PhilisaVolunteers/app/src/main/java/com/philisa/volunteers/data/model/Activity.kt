package com.philisa.volunteers.data.model

import com.google.firebase.firestore.DocumentId

/**
 * Represents a volunteer opportunity (Figs 58-61, 72-73) — NOT an android.app.Activity.
 * Kept as `Activity.kt` per the approved file structure; no file in this project both
 * extends android.app.Activity and references this model in the same source file, so the
 * name collision flagged in Stage 1 never actually manifests at a call site.
 */
data class Activity(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val programme: String = "",
    val date: String = "",
    val dateMillis: Long = 0L,
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val volunteerRole: String = "",
    val totalSpots: Int = 0,
    val filledSpots: Int = 0,
    val description: String = "",
    val status: String = STATUS_DRAFT,
    val createdBy: String = "",
    val createdDate: Long = 0L
) {
    val spotsRemaining: Int get() = (totalSpots - filledSpots).coerceAtLeast(0)
    val dateTimeLabel: String get() = "$date · $startTime - $endTime"

    companion object {
        const val STATUS_DRAFT = "draft"
        const val STATUS_PUBLISHED = "published"
    }
}
