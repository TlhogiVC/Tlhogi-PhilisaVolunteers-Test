package com.philisa.volunteers.navigation

/**
 * Top-level destinations the app can land on after Splash/Login. The auth/application flow
 * (Figs 49-55) is a chain of separate Activities per the approved file structure, navigated via
 * explicit Intents (see [AppNavGraph], [AuthNavGraph]) rather than a single Fragment NavHost —
 * Activity-suffixed classes with their own activity_*.xml layouts are not Fragment destinations.
 */
object Routes {
    const val WELCOME = "route_welcome"
    const val LOGIN = "route_login"
    const val PERSONAL_DETAILS = "route_personal_details"
    const val PROGRAMME_INTEREST = "route_programme_interest"
    const val REVIEW_APPLICATION = "route_review_application"
    const val APPLICATION_SUCCESS = "route_application_success"
    const val VOLUNTEER_MAIN = "route_volunteer_main"
    const val ADMIN_MAIN = "route_admin_main"
}
