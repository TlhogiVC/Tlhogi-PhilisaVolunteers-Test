package com.philisa.volunteers.navigation

import android.os.Bundle
import androidx.navigation.NavController
import com.philisa.volunteers.R
import com.philisa.volunteers.utils.Constants

/** Typed navigation helpers for res/navigation/admin_nav_graph.xml. */
object AdminNavGraph {

    fun toVolunteerDetails(navController: NavController, applicationId: String) {
        val args = Bundle().apply { putString(Constants.EXTRA_VOLUNTEER_APPLICATION_ID, applicationId) }
        navController.navigate(R.id.action_global_volunteerDetailsFragment, args)
    }

    fun toCreateActivity(navController: NavController) {
        navController.navigate(R.id.action_global_createActivityFragment)
    }

    fun toEditActivity(navController: NavController, activityId: String) {
        val args = Bundle().apply { putString(Constants.EXTRA_ACTIVITY_ID, activityId) }
        navController.navigate(R.id.action_global_editActivityFragment, args)
    }

    fun toCreateAnnouncement(navController: NavController) {
        navController.navigate(R.id.action_global_createAnnouncementFragment)
    }

    fun toApplicantDetails(navController: NavController, activityApplicationId: String) {
        val args = Bundle().apply { putString(Constants.EXTRA_ACTIVITY_APPLICATION_ID, activityApplicationId) }
        navController.navigate(R.id.action_global_applicantDetailsFragment, args)
    }

    fun toManageImpactStats(navController: NavController) {
        navController.navigate(R.id.action_global_manageImpactStatsFragment)
    }

    fun toManageGallery(navController: NavController) {
        navController.navigate(R.id.action_global_manageGalleryFragment)
    }
}
