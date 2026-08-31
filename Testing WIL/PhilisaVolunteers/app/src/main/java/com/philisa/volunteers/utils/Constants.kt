package com.philisa.volunteers.utils

object Constants {
    // Firestore collection names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_VOLUNTEER_APPLICATIONS = "volunteerApplications"
    const val COLLECTION_ACTIVITIES = "activities"
    const val COLLECTION_ACTIVITY_APPLICATIONS = "activityApplications"
    const val COLLECTION_ANNOUNCEMENTS = "announcements"
    const val COLLECTION_IMPACT_STATS = "impactStats"
    const val COLLECTION_GALLERY = "gallery"
    const val COLLECTION_ADMIN_LOGIN_ATTEMPTS = "adminLoginAttempts"

    const val IMPACT_STATS_DOC_ID = "current"
    const val STORAGE_GALLERY_PATH = "gallery"
    const val SECONDARY_APP_NAME = "PhilisaVolunteerProvisioning"

    // Intent extras
    const val EXTRA_SELECTED_ROLE = "extra_selected_role"
    const val EXTRA_VOLUNTEER_APPLICATION_ID = "extra_volunteer_application_id"
    const val EXTRA_ACTIVITY_ID = "extra_activity_id"
    const val EXTRA_ANNOUNCEMENT_ID = "extra_announcement_id"
    const val EXTRA_ACTIVITY_APPLICATION_ID = "extra_activity_application_id"

    // Application step hand-off — forwarded as Intent extras between PersonalDetailsActivity,
    // ProgrammeInterestActivity and ReviewApplicationActivity
    const val EXTRA_FIRST_NAME = "extra_first_name"
    const val EXTRA_LAST_NAME = "extra_last_name"
    const val EXTRA_EMAIL = "extra_email"
    const val EXTRA_PHONE = "extra_phone"
    const val EXTRA_AREA = "extra_area"
    const val EXTRA_PROGRAMME_INTEREST = "extra_programme_interest"
    const val EXTRA_MOTIVATION = "extra_motivation"

    // Roles
    const val ROLE_VOLUNTEER = "volunteer"
    const val ROLE_ADMIN = "admin"
}
