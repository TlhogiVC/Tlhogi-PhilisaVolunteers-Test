package com.philisa.volunteers.data.repository

import com.philisa.volunteers.data.firebase.FirestoreManager
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.tasks.await

/**
 * Handles both "become a volunteer" applications ([VolunteerApplication], Figs 51-54, 70-71) and
 * activity sign-ups ([ActivityApplication], Figs 58-61, 76-77) — the approved file structure has
 * a single ApplicationRepository slot, so both concerns are consolidated here rather than adding
 * a new repository file.
 */
class ApplicationRepository {

    private val db = FirestoreManager.db
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val activityRepository = ActivityRepository()

    private fun volunteerApplications() = db.collection(Constants.COLLECTION_VOLUNTEER_APPLICATIONS)
    private fun activityApplications() = db.collection(Constants.COLLECTION_ACTIVITY_APPLICATIONS)

    // ---------------------------------------------------------------------
    // Volunteer applications (Apply Step 1-3)
    // ---------------------------------------------------------------------

    suspend fun submitApplication(application: VolunteerApplication): Result<VolunteerApplication> {
        return try {
            val toSave = application.copy(
                status = VolunteerApplication.STATUS_PENDING,
                referenceNumber = DateUtils.generateReferenceNumber(),
                appliedDate = DateUtils.now()
            )
            val ref = volunteerApplications().add(toSave).await()
            Result.success(toSave.copy(id = ref.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApplication(id: String): VolunteerApplication? {
        return volunteerApplications().document(id).get().await().toObject(VolunteerApplication::class.java)
    }

    suspend fun getApplicationsByStatus(status: String?): List<VolunteerApplication> {
        val query = if (status == null) {
            volunteerApplications()
        } else {
            volunteerApplications().whereEqualTo("status", status)
        }
        return query.get().await().toObjects(VolunteerApplication::class.java)
    }

    suspend fun approveApplication(application: VolunteerApplication, reviewerUid: String): Result<Unit> {
        return try {
            val provisionResult = authRepository.provisionVolunteerAccount(application.email)
            val uid = provisionResult.getOrElse { return Result.failure(it) }

            userRepository.createUserFromApplication(uid, application)
                .getOrElse { return Result.failure(it) }

            volunteerApplications().document(application.id)
                .update(
                    mapOf(
                        "status" to VolunteerApplication.STATUS_APPROVED,
                        "reviewedDate" to DateUtils.now(),
                        "reviewedBy" to reviewerUid
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectApplication(applicationId: String, reviewerUid: String): Result<Unit> {
        return try {
            volunteerApplications().document(applicationId)
                .update(
                    mapOf(
                        "status" to VolunteerApplication.STATUS_REJECTED,
                        "reviewedDate" to DateUtils.now(),
                        "reviewedBy" to reviewerUid
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------------------------------------------------------------
    // Activity sign-ups (apply / cancel / confirm / remove)
    // ---------------------------------------------------------------------

    suspend fun getMyActivityApplication(activityId: String, userId: String): ActivityApplication? {
        return activityApplications()
            .whereEqualTo("activityId", activityId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(ActivityApplication::class.java)
            .firstOrNull { it.status != ActivityApplication.STATUS_CANCELLED }
    }

    suspend fun getMyActivityApplications(userId: String): List<ActivityApplication> {
        return activityApplications()
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(ActivityApplication::class.java)
    }

    suspend fun getActivityApplicationsForActivity(activityId: String): List<ActivityApplication> {
        return activityApplications()
            .whereEqualTo("activityId", activityId)
            .get()
            .await()
            .toObjects(ActivityApplication::class.java)
    }

    suspend fun applyForActivity(activity: Activity, user: User): Result<Unit> {
        return try {
            if (activity.spotsRemaining <= 0) {
                return Result.failure(IllegalStateException("error_spots_full"))
            }
            val application = ActivityApplication(
                activityId = activity.id,
                userId = user.uid,
                volunteerName = user.fullName,
                status = ActivityApplication.STATUS_PENDING,
                appliedDate = DateUtils.now()
            )
            activityApplications().add(application).await()
            activityRepository.updateFilledSpots(activity.id, +1)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelActivityApplication(activityApplication: ActivityApplication): Result<Unit> {
        return try {
            activityApplications().document(activityApplication.id)
                .update("status", ActivityApplication.STATUS_CANCELLED)
                .await()
            activityRepository.updateFilledSpots(activityApplication.activityId, -1)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmActivityApplication(activityApplication: ActivityApplication): Result<Unit> {
        return try {
            activityApplications().document(activityApplication.id)
                .update("status", ActivityApplication.STATUS_CONFIRMED)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeActivityApplication(activityApplication: ActivityApplication): Result<Unit> {
        return try {
            activityApplications().document(activityApplication.id).delete().await()
            if (activityApplication.status != ActivityApplication.STATUS_CANCELLED) {
                activityRepository.updateFilledSpots(activityApplication.activityId, -1)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
