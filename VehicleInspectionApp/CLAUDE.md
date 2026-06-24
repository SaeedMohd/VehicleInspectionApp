# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Debug builds
./gradlew assembleUatDebug
./gradlew assembleProductionDebug

# Release builds
./gradlew assembleUatRelease
./gradlew assembleProductionRelease

# Build all variants
./gradlew assembleDebug
./gradlew assembleRelease

# Clean
./gradlew clean
```

There are no test dependencies or test directories — the project has no automated tests.

## Architecture Overview

**Vehicle inspection and facility management Android app** (`com.inspection`, package namespace). Targets API 34, minSdk 24, written in mixed Kotlin/Java.

### Entry Points & Navigation Flow

- **LoginActivity** — LAUNCHER activity, handles authentication against the configured API
- **MainActivity** — Primary hub after login; hosts most fragments, manages location tracking and Bluetooth
- **FormsActivity** — Inspection form entry
- **ApplicantActivity** — Applicant/facility information display

The app uses ~60 fragments hosted in activities. Despite having the Navigation Component dependency, navigation is largely manual (direct fragment transactions).

### State Management

State is held in global singletons — there is no ViewModel/LiveData architecture in practice:

- **`FacilityDataModel`** — synchronized singleton holding the current facility's data; the central data store passed throughout the app
- **`ApplicationPrefs.java`** — SharedPreferences singleton (~32K lines); stores session, user, and cached facility data
- **`AnnualVisitationSingleton`** — holds annual visitation state

### Networking

All API calls use `AsyncTask` (deprecated) in `serverTasks/` (29 task classes). There is no Retrofit. Volley is the HTTP library for some requests; OkHttp is imported but used selectively.

API base URLs are in `Utils/Constants.kt`:
- Dev: `http://144.217.24.163:5001/`
- UAT: `https://inspectionuat.jet-matics.com/`
- Production: `https://inspection.valueaddedonline.com/`

The active URL is selected at runtime based on `BuildConfig.FLAVOR` (`uat` or `production`).

### Build Flavors

| Flavor | applicationIdSuffix | API |
|---|---|---|
| `uat` | `.uat` | UAT server |
| `production` | *(none)* | Production server |

Build types: `debug` (debuggable) and `release` (ProGuard minification, `proguard-rules.pro`).

### Key Utility Files

- **`Utils/Utils.kt`** (~7K lines) — catch-all utility functions; look here before writing new helpers
- **`Utils/Utility.java`** (~49K lines) — legacy Java utilities, networking helpers
- **`Utils/ApplicationPrefs.java`** (~33K lines) — all SharedPreferences keys and accessors

### Key Libraries

| Purpose | Library |
|---|---|
| HTTP | Volley 1.2.1, OkHttp 5.3.2 |
| JSON | Gson 2.8.9 |
| Image loading | Glide 5.0.5 |
| Maps | Google Maps 20.0.0 |
| Location | Play Services Location 21.3.0 |
| PDF viewing | android-pdf-viewer 3.2.0-beta.3 |
| PDF generation | iText 5.5.10 |
| Charts | MPAndroidChart v3.1.0 |
| Animations | Lottie 6.7.1 |
| Crash reporting | Firebase Crashlytics (BOM 34.9.0) |
| Remote logging | Bugfender 4.x (separate keys per flavor in `CustomApplication`) |
| Signature capture | simplify/ink 1.0.0 |
| Multi-step forms | vertical-stepper-form 2.7.0 |

### Networking / Cleartext

`network_security_config.xml` permits cleartext for `firebase.google.com` and `*.jet-matics.com`. All other traffic requires HTTPS.

### Signing

Release keystore is `VehicleHealthMonitor.keystore`, configured directly in `app/build.gradle` — both debug and release build types use the same release keystore.

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan
at `specs/009-specialists-perf-pdf/plan.md`.
<!-- SPECKIT END -->
