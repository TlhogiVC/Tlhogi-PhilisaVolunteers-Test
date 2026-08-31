package com.philisa.volunteers.navigation

import android.app.Activity
import android.content.Intent
import com.philisa.volunteers.admin.AdminMainActivity
import com.philisa.volunteers.ui.auth.LoginChoiceActivity
import com.philisa.volunteers.ui.auth.application.ApplicationSuccessActivity
import com.philisa.volunteers.ui.auth.application.PersonalDetailsActivity
import com.philisa.volunteers.ui.auth.application.ProgrammeInterestActivity
import com.philisa.volunteers.ui.auth.application.ReviewApplicationActivity
import com.philisa.volunteers.ui.volunteer.VolunteerMainActivity
import com.philisa.volunteers.utils.Constants

/** Navigation between the Welcome/Login/Apply-step screens (Figs 49-55). */
object AuthNavGraph {

    fun goToLogin(activity: Activity) {
        activity.startActivity(Intent(activity, LoginChoiceActivity::class.java))
    }

    fun goToPersonalDetails(activity: Activity) {
        activity.startActivity(Intent(activity, PersonalDetailsActivity::class.java))
    }

    fun goToProgrammeInterest(activity: Activity, firstName: String, lastName: String, email: String, phone: String, area: String) {
        val intent = Intent(activity, ProgrammeInterestActivity::class.java).apply {
            putExtra(Constants.EXTRA_FIRST_NAME, firstName)
            putExtra(Constants.EXTRA_LAST_NAME, lastName)
            putExtra(Constants.EXTRA_EMAIL, email)
            putExtra(Constants.EXTRA_PHONE, phone)
            putExtra(Constants.EXTRA_AREA, area)
        }
        activity.startActivity(intent)
    }

    fun goToReviewApplication(
        activity: Activity,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        area: String,
        programmeInterest: String,
        motivation: String
    ) {
        val intent = Intent(activity, ReviewApplicationActivity::class.java).apply {
            putExtra(Constants.EXTRA_FIRST_NAME, firstName)
            putExtra(Constants.EXTRA_LAST_NAME, lastName)
            putExtra(Constants.EXTRA_EMAIL, email)
            putExtra(Constants.EXTRA_PHONE, phone)
            putExtra(Constants.EXTRA_AREA, area)
            putExtra(Constants.EXTRA_PROGRAMME_INTEREST, programmeInterest)
            putExtra(Constants.EXTRA_MOTIVATION, motivation)
        }
        activity.startActivity(intent)
    }

    fun goToApplicationSuccess(activity: Activity, applicationId: String) {
        val intent = Intent(activity, ApplicationSuccessActivity::class.java).apply {
            putExtra(Constants.EXTRA_VOLUNTEER_APPLICATION_ID, applicationId)
        }
        activity.startActivity(intent)
        activity.finish()
    }

    fun goToVolunteerMain(activity: Activity) {
        activity.startActivity(Intent(activity, VolunteerMainActivity::class.java))
        activity.finish()
    }

    fun goToAdminMain(activity: Activity) {
        activity.startActivity(Intent(activity, AdminMainActivity::class.java))
        activity.finish()
    }
}
