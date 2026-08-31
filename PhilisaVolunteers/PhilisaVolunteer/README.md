# Philisa Volunteer — Android Studio Project Guide

Native Android companion app to the Philisa Abafazi Bethu Women Centre SA
web Help Centre, built from the prototype mockups in `Android_Prototypes.pdf`
(System Design Document §11.3). Two roles share one app: **Volunteer** and
**Admin**, each with a 5-tab bottom navigation bar.

## 1. Create the project in Android Studio

Don't hand-roll `build.gradle` / the Gradle wrapper — let the wizard do it,
then copy the `res/` and manifest content below into what it generates.

1. **File → New → New Project → Empty Views Activity**
2. Name: `Philisa Volunteer` · Package name: `com.philisa.volunteer`
3. Language: **Kotlin** · Minimum SDK: **API 26 (Android 8.0)** — safe
   floor for `Material Components` + `Navigation Component` without extra
   backport work
4. Build configuration language: **Kotlin DSL (build.gradle.kts)**

Then add to `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
}

android {
    buildFeatures {
        viewBinding = true   // used to reference views from Kotlin, incl. <include> ids
    }
}
```

## 2. Target folder structure

```
PhilisaVolunteer/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/philisa/volunteer/
│       │   │   ├── OnboardingActivity.kt      — hosts nav_graph_onboarding
│       │   │   ├── MainActivity.kt            — volunteer host (bottom nav)
│       │   │   ├── AdminActivity.kt           — admin host (bottom nav)
│       │   │   ├── onboarding/                — WelcomeFragment, LandingFragment
│       │   │   ├── auth/                      — LoginFragment, AdminLoginFragment
│       │   │   ├── application/                — ApplyStep1/2/3Fragment, ApplicationStatusFragment, ApplicationViewModel
│       │   │   ├── home/                      — HomeFragment (volunteer dashboard)
│       │   │   ├── activities/                — ActivitiesFragment, ActivityDetailBottomSheet
│       │   │   ├── schedule/                  — ScheduleFragment
│       │   │   ├── community/                 — CommunityFragment
│       │   │   ├── profile/                   — ProfileFragment
│       │   │   ├── admin/                     — AdminOverviewFragment, ManageVolunteersFragment, ManageActivitiesFragment, ManageAnnouncementsFragment, ActivityApplicationsFragment
│       │   │   ├── data/                      — data classes: Volunteer, ActivityListing, Announcement, Application
│       │   │   └── common/                    — shared RecyclerView adapters, extension functions
│       │   └── res/
│       │       ├── layout/                    — fragment_*.xml, item_*.xml, partial_*.xml  ✅ this batch
│       │       ├── navigation/                — nav_graph_onboarding.xml ✅, nav_graph_volunteer.xml, nav_graph_admin.xml
│       │       ├── menu/                      — bottom_nav_volunteer.xml, bottom_nav_admin.xml
│       │       ├── drawable/                  — icons + shape backgrounds  ✅ this batch
│       │       ├── values/                    — colors.xml, strings.xml, themes.xml, dimens.xml  ✅ this batch
│       │       └── mipmap/                    — app launcher icon
│       ├── test/                              — JVM unit tests (ViewModels, data mappers)
│       └── androidTest/                       — instrumented UI tests (Espresso)
└── README.md                                  — this file
```

**Why two "host" Activities (`MainActivity` for volunteers, `AdminActivity`
for admins) instead of one?** The two roles have completely different
bottom-nav tab sets (Home/Activities/Schedule/Community/Profile vs.
Overview/Volunteers/Activities/Posts/Applied — Figs 56 and 68). Splitting
them keeps each `NavHostFragment` + `BottomNavigationView` pairing simple,
rather than conditionally hiding/showing tabs in one shared shell.
`OnboardingActivity` is the launcher; after a successful login it starts
`MainActivity` or `AdminActivity` based on the tab selected on the login
screen (Fig 55) and finishes itself so the back button can't return to
onboarding.

## 3. What's included in this pass

Package-agnostic **XML only** (no Kotlin classes yet — see §5) for the
**Onboarding + Volunteer Application flow**, Figures 49–54:

| File | Figure |
|---|---|
| `res/layout/fragment_welcome.xml` | 49 — Welcome/splash |
| `res/layout/fragment_landing.xml` | 50 — Landing (Become a Volunteer / Login) |
| `res/layout/partial_step_header.xml` | shared purple step header (Figs 51-53) |
| `res/layout/fragment_apply_step1_details.xml` | 51 — Your Details |
| `res/layout/fragment_apply_step2_interests.xml` | 52 — Your Interests |
| `res/layout/item_interest_option.xml` | programme-interest card used in Fig 52 |
| `res/layout/fragment_apply_step3_review.xml` | 53 — Review & Submit |
| `res/layout/item_summary_row.xml` | label/value row used in Fig 53 |
| `res/layout/fragment_application_status.xml` | 54 — Application Status |
| `res/layout/item_timeline_step.xml` | timeline row used in Fig 54 |
| `res/navigation/nav_graph_onboarding.xml` | wires the 6 screens above together |
| `res/values/colors.xml`, `dimens.xml`, `strings.xml`, `themes.xml` | shared design tokens |
| `res/drawable/*` | gradients, pills, progress segments, icons |
| `AndroidManifest.xml` | 3-Activity skeleton (Onboarding/Main/Admin) |

## 4. Remaining screens (future batches)

- **Auth:** Fig 55 (Volunteer/Admin login toggle), Fig 67 (Admin login)
- **Volunteer app:** Fig 56-57 (Home), 58-61 (Activities + detail sheet),
  62 (Schedule), 63-64 (Community), 65 (Profile)
- **Admin app:** Fig 68-69 (Overview), 70-71 (Manage Volunteers + detail),
  72-73 (Manage Activities + create form), 74-75 (Manage Announcements +
  create form), 76-77 (Activity Applications + applicant detail)

Say the word when you want the next flow generated — I'd suggest **Auth**
next since `loginFragment` is already stubbed (unconnected) in
`nav_graph_onboarding.xml`, then **Volunteer app**, then **Admin app**.

## 5. Placeholders you'll want to swap

- **Photos** (Welcome background, About Us image): currently solid-colour
  fallbacks. Drop real `.webp`/`.jpg` assets into `res/drawable` and update
  `android:src` in `fragment_welcome.xml` / `fragment_landing.xml`.
- **Programme icons** (`ic_programme_placeholder.xml`): one generic
  sparkle glyph reused for all 6 interest cards. Replace per-programme via
  **Android Studio → File → New → Vector Asset** (suggested Material
  Symbols: `favorite`, `groups`, `work`, `restaurant`, `shield`,
  `medical_services`).
- **Kotlin Fragment classes**: each `fragment_*.xml` needs a matching
  `Fragment` subclass (e.g. `WelcomeFragment`) that inflates it via View
  Binding — these aren't generated yet since the request was XML-first.

## 6. Design tokens quick reference

| Token | Value | Use |
|---|---|---|
| `purple_900` → `purple_500` | `#3B0764` → `#7C3AED` | header gradient |
| `background_cream` | `#FDFBF5` | screen background |
| `status_success_bg/text` | `#D1FAE5` / `#059669` | Approved/Confirmed/Published pills |
| `status_pending_bg/text` | `#FEF3C7` / `#D97706` | Pending/Under Review pills |
| `status_error_bg/text` | `#FEE2E2` / `#DC2626` | Rejected pills, destructive actions |
