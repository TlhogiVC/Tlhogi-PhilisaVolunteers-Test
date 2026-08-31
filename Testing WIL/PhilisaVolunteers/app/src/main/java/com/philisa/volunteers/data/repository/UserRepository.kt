package com.philisa.volunteers.data.repository

import com.philisa.volunteers.data.firebase.FirestoreManager
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirestoreManager.db
    private fun usersCollection() = db.collection(Constants.COLLECTION_USERS)

    suspend fun getUser(uid: String): User? {
        return usersCollection().document(uid).get().await().toObject(User::class.java)
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection().document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Creates the users/{uid} profile document when a [VolunteerApplication] is approved. */
    suspend fun createUserFromApplication(uid: String, application: VolunteerApplication): Result<Unit> {
        return try {
            val user = User(
                uid = uid,
                firstName = application.firstName,
                lastName = application.lastName,
                email = application.email,
                phone = application.phone,
                area = application.area,
                role = User.ROLE_VOLUNTEER,
                volunteerId = DateUtils.generateVolunteerId(),
                skills = emptyList(),
                availability = emptyList(),
                joinedDate = DateUtils.now()
            )
            usersCollection().document(uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getApprovedVolunteers(): List<User> {
        return usersCollection()
            .whereEqualTo("role", User.ROLE_VOLUNTEER)
            .get()
            .await()
            .toObjects(User::class.java)
    }
}
