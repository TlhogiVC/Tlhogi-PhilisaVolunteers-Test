package com.philisa.volunteers.data.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.philisa.volunteers.utils.Constants
import kotlinx.coroutines.tasks.await

/**
 * Wraps Firebase Authentication. [createVolunteerAccountIsolated] uses a secondary,
 * non-default [FirebaseApp] instance so provisioning a new volunteer's account never
 * signs the currently-authenticated admin out of their own session (Firebase's client SDK
 * signs the caller in as whichever account [FirebaseAuth.createUserWithEmailAndPassword]
 * just created, so it must run on an isolated Auth instance — see Stage 1 decisions).
 */
object FirebaseAuthManager {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        return result.user ?: throw IllegalStateException("Sign-in succeeded but returned no user")
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    /**
     * Creates a new Firebase Auth account on an isolated secondary app instance, immediately
     * sends that account a "set your password" reset email, then tears the secondary instance
     * down. The admin's own [auth] session (default app) is never touched.
     */
    suspend fun createVolunteerAccountIsolated(email: String, temporaryPassword: String): String {
        val secondaryApp = FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext)
            .firstOrNull { it.name == Constants.SECONDARY_APP_NAME }
            ?: FirebaseApp.initializeApp(
                FirebaseApp.getInstance().applicationContext,
                FirebaseApp.getInstance().options,
                Constants.SECONDARY_APP_NAME
            )

        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
        try {
            val result = secondaryAuth.createUserWithEmailAndPassword(email.trim(), temporaryPassword).await()
            val uid = result.user?.uid
                ?: throw IllegalStateException("Account creation succeeded but returned no user")
            secondaryAuth.sendPasswordResetEmail(email.trim()).await()
            return uid
        } finally {
            secondaryAuth.signOut()
        }
    }
}
