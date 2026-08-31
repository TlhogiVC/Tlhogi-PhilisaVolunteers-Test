package com.philisa.volunteers.data.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.philisa.volunteers.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID

object StorageManager {

    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    /** Uploads [imageUri] to Storage and returns a Pair of (downloadUrl, storagePath). */
    suspend fun uploadGalleryImage(imageUri: Uri): Pair<String, String> {
        val path = "${Constants.STORAGE_GALLERY_PATH}/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(path)
        ref.putFile(imageUri).await()
        val downloadUrl = ref.downloadUrl.await().toString()
        return downloadUrl to path
    }

    suspend fun deleteGalleryImage(storagePath: String) {
        if (storagePath.isBlank()) return
        storage.reference.child(storagePath).delete().await()
    }
}
