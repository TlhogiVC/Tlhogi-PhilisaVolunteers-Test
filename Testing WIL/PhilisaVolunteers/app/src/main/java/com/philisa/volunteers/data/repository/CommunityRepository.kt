package com.philisa.volunteers.data.repository

import android.net.Uri
import com.philisa.volunteers.data.firebase.FirestoreManager
import com.philisa.volunteers.data.firebase.StorageManager
import com.philisa.volunteers.data.model.GalleryItem
import com.philisa.volunteers.data.model.ImpactStats
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.tasks.await

/** Handles the two Community-page (Figs 63-64) data sources not owned by another repository:
 *  impact statistics and the photo gallery. Announcements are handled by AnnouncementRepository. */
class CommunityRepository {

    private val db = FirestoreManager.db
    private fun galleryCollection() = db.collection(Constants.COLLECTION_GALLERY)
    private fun impactStatsDoc() = db.collection(Constants.COLLECTION_IMPACT_STATS)
        .document(Constants.IMPACT_STATS_DOC_ID)

    suspend fun getImpactStats(): ImpactStats {
        val snapshot = impactStatsDoc().get().await()
        return snapshot.toObject(ImpactStats::class.java) ?: ImpactStats()
    }

    suspend fun updateImpactStats(stats: ImpactStats): Result<Unit> {
        return try {
            impactStatsDoc().set(stats).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGalleryItems(): List<GalleryItem> {
        return galleryCollection()
            .get()
            .await()
            .toObjects(GalleryItem::class.java)
            .sortedByDescending { it.uploadedDate }
    }

    suspend fun addGalleryItem(imageUri: Uri, caption: String): Result<Unit> {
        return try {
            val (downloadUrl, storagePath) = StorageManager.uploadGalleryImage(imageUri)
            val item = GalleryItem(
                imageUrl = downloadUrl,
                storagePath = storagePath,
                caption = caption,
                uploadedDate = DateUtils.now()
            )
            galleryCollection().add(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGalleryItem(item: GalleryItem): Result<Unit> {
        return try {
            StorageManager.deleteGalleryImage(item.storagePath)
            galleryCollection().document(item.id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
