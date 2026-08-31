package com.philisa.volunteers.data.repository

import com.google.firebase.firestore.FirebaseFirestoreException
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.firebase.FirestoreManager
import com.philisa.volunteers.data.model.AdminLoginAttempt
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRepository {

    private val db = FirestoreManager.db

    val currentUserId: String? get() = FirebaseAuthManager.currentUser?.uid

    /**
     * Signs in and verifies the account's stored role matches [expectedRole] (the tab the user
     * picked on the login screen, Fig 55/67). A mismatch signs the user back out immediately —
     * the toggle is a real access boundary, not just a label.
     */
    suspend fun signIn(email: String, password: String, expectedRole: String): Result<User> {
        return try {
            val firebaseUser = FirebaseAuthManager.signIn(email, password)
            val userDoc = db.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()
            val user = userDoc.toObject(User::class.java)

            if (user == null) {
                FirebaseAuthManager.signOut()
                if (expectedRole == Constants.ROLE_ADMIN) logAdminLoginAttempt(email, success = false)
                return Result.failure(IllegalStateException("error_account_not_found"))
            }
            if (user.role != expectedRole) {
                FirebaseAuthManager.signOut()
                if (expectedRole == Constants.ROLE_ADMIN) logAdminLoginAttempt(email, success = false)
                return Result.failure(IllegalStateException("error_wrong_role"))
            }
            if (expectedRole == Constants.ROLE_ADMIN) logAdminLoginAttempt(email, success = true)
            Result.success(user)
        } catch (e: Exception) {
            if (expectedRole == Constants.ROLE_ADMIN) logAdminLoginAttempt(email, success = false)
            Result.failure(e)
        }
    }

    fun signOut() {
        FirebaseAuthManager.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            FirebaseAuthManager.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates the Firebase Auth account for a newly approved volunteer and emails them a
     * password-reset link so they can set their own password (Stage 1 decision — Apply Step 1
     * never collects a password). Returns the new account's uid.
     */
    suspend fun provisionVolunteerAccount(email: String): Result<String> {
        return try {
            val temporaryPassword = UUID.randomUUID().toString()
            val uid = FirebaseAuthManager.createVolunteerAccountIsolated(email, temporaryPassword)
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserRole(uid: String): String? {
        return try {
            db.collection(Constants.COLLECTION_USERS).document(uid).get().await()
                .toObject(User::class.java)?.role
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    private suspend fun logAdminLoginAttempt(email: String, success: Boolean) {
        try {
            val attempt = AdminLoginAttempt(email = email.trim(), success = success, timestamp = DateUtils.now())
            db.collection(Constants.COLLECTION_ADMIN_LOGIN_ATTEMPTS).add(attempt).await()
        } catch (_: Exception) {
            // Logging must never block or fail the sign-in flow itself.
        }
    }
}
