package com.philisa.volunteers.navigation

import android.app.Activity
import android.content.Intent
import com.philisa.volunteers.admin.AdminMainActivity
import com.philisa.volunteers.ui.auth.SplashActivity
import com.philisa.volunteers.ui.auth.WelcomeActivity
import com.philisa.volunteers.ui.volunteer.VolunteerMainActivity
import com.philisa.volunteers.utils.Constants

/**
 * Root-level navigation used by MainActivity/SplashActivity to route the user to their
 * post-launch destination based on cached auth state (see Routes for the full destination map).
 */
object AppNavGraph {

    fun goToSplash(activity: Activity) {
        activity.startActivity(Intent(activity, SplashActivity::class.java))
        activity.finish()
    }

    fun goToWelcome(activity: Activity) {
        activity.startActivity(Intent(activity, WelcomeActivity::class.java))
        activity.finish()
    }

    fun goToRoleHome(activity: Activity, role: String) {
        val destination = if (role == Constants.ROLE_ADMIN) {
            Intent(activity, AdminMainActivity::class.java)
        } else {
            Intent(activity, VolunteerMainActivity::class.java)
        }
        activity.startActivity(destination)
        activity.finish()
    }
}
