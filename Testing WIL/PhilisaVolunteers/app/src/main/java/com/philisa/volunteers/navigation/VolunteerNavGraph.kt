package com.philisa.volunteers.navigation

import android.os.Bundle
import androidx.navigation.NavController
import com.philisa.volunteers.R
import com.philisa.volunteers.utils.Constants

/** Typed navigation helpers for res/navigation/volunteer_nav_graph.xml. */
object VolunteerNavGraph {

    fun toActivityDetails(navController: NavController, activityId: String) {
        val args = Bundle().apply { putString(Constants.EXTRA_ACTIVITY_ID, activityId) }
        navController.navigate(R.id.action_global_activityDetailsFragment, args)
    }

    fun toAnnouncementDetails(navController: NavController, announcementId: String) {
        val args = Bundle().apply { putString(Constants.EXTRA_ANNOUNCEMENT_ID, announcementId) }
        navController.navigate(R.id.action_global_announcementDetailsFragment, args)
    }

    fun toEditProfile(navController: NavController) {
        navController.navigate(R.id.action_profileFragment_to_editProfileFragment)
    }
}
