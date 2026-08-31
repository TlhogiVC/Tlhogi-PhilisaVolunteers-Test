package com.philisa.volunteers.data.repository

import com.philisa.volunteers.data.firebase.FirestoreManager
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.tasks.await

class ActivityRepository {

    private val db = FirestoreManager.db
    private fun activitiesCollection() = db.collection(Constants.COLLECTION_ACTIVITIES)

    suspend fun createActivity(activity: Activity, createdBy: String): Result<String> {
        return try {
            val toSave = activity.copy(createdBy = createdBy, createdDate = DateUtils.now())
            val ref = activitiesCollection().add(toSave).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateActivity(activity: Activity): Result<Unit> {
        return try {
            activitiesCollection().document(activity.id).set(activity).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setPublishStatus(activityId: String, status: String): Result<Unit> {
        return try {
            activitiesCollection().document(activityId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFilledSpots(activityId: String, delta: Int): Result<Unit> {
        return try {
            val docRef = activitiesCollection().document(activityId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val current = snapshot.getLong("filledSpots") ?: 0L
                val updated = (current + delta).coerceAtLeast(0)
                transaction.update(docRef, "filledSpots", updated)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteActivity(activityId: String): Result<Unit> {
        return try {
            activitiesCollection().document(activityId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActivity(activityId: String): Activity? {
        return activitiesCollection().document(activityId).get().await().toObject(Activity::class.java)
    }

    suspend fun getPublishedActivities(): List<Activity> {
        return activitiesCollection()
            .whereEqualTo("status", Activity.STATUS_PUBLISHED)
            .get()
            .await()
            .toObjects(Activity::class.java)
    }

    suspend fun getAllActivities(): List<Activity> {
        return activitiesCollection().get().await().toObjects(Activity::class.java)
    }
}
