package com.philisa.volunteers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.philisa.volunteers.data.firebase.FirebaseAuthManager
import com.philisa.volunteers.data.repository.AuthRepository
import com.philisa.volunteers.navigation.AppNavGraph
import kotlinx.coroutines.launch

/**
 * Manifest LAUNCHER. Has no layout of its own (no activity_main.xml in the approved resource
 * list) — it only decides where to send the user based on cached Firebase Auth state, then
 * finishes: SplashActivity's branded screen for a logged-out user, or straight to
 * VolunteerMainActivity/AdminMainActivity (skipping Welcome/Login entirely) for one already
 * signed in. This keeps SplashActivity meaningful rather than redundant (see Stage 1 decisions).
 */
class MainActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuthManager.currentUser
        if (currentUser == null) {
            AppNavGraph.goToSplash(this)
            return
        }

        lifecycleScope.launch {
            val role = authRepository.fetchUserRole(currentUser.uid)
            if (role == null) {
                authRepository.signOut()
                AppNavGraph.goToSplash(this@MainActivity)
            } else {
                AppNavGraph.goToRoleHome(this@MainActivity, role)
            }
        }
    }
}
