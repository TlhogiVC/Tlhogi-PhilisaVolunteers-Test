package com.philisa.volunteers.data.model

import com.google.firebase.firestore.DocumentId

data class GalleryItem(
    @DocumentId
    val id: String = "",
    val imageUrl: String = "",
    val storagePath: String = "",
    val caption: String = "",
    val uploadedDate: Long = 0L
)
