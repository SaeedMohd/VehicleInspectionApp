# Implementation Plan: Login Screen Version Update Notification

**Branch**: `005-login-version-notification` | **Date**: 2026-06-10 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/005-login-version-notification/spec.md`

## Summary

Add a non-blocking Firebase Remote Config check called from `LoginActivity.onResume()` that compares `BuildConfig.VERSION_CODE` against `latest_version_code` from Remote Config and shows a dismissable `AlertDialog` when an update is available. Placing the call in `onResume()` (not `onCreate()`) ensures the dialog re-evaluates every time the login screen becomes active — including background-resume scenarios — satisfying the spec edge case "user dismisses, backgrounds, reopens → dialog reappears." Firebase RC's 1-hour minimum fetch interval means `fetchAndActivate()` returns from cache on repeated calls, so there is no network redundancy. No forced update, no persisted state.

## Technical Context

**Language/Version**: Kotlin (mixed Kotlin/Java project), Android API 34, minSdk 24

**Primary Dependencies**:
- Firebase BOM 34.9.0 (already present)
- `com.google.firebase:firebase-config` (new addition via existing BOM — no version bump needed)
- `androidx.appcompat.app.AlertDialog.Builder` (already used in `LoginActivity.kt`)

**Storage**: None — no local persistence for this feature

**Testing**: No automated tests (project has none)

**Target Platform**: Android 7.0+ (minSdk 24), both `uat` and `production` flavors

**Project Type**: Mobile app — single Activity touch point (`LoginActivity.kt`)

**Performance Goals**: Login screen must remain interactive immediately; Remote Config fetch is async and must not block UI thread

**Constraints**: Dialog must not block login under any failure/timeout condition

**Scale/Scope**: Single file change (`LoginActivity.kt`) + one dependency line in `app/build.gradle`

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| Fragment-based UI | ✅ Pass | Feature lives entirely in `LoginActivity` — no new Activity or Fragment |
| Singleton state | ✅ Pass | No new singleton introduced; Remote Config SDK manages its own caching |
| Flavor-aware endpoints | ✅ Pass | Both flavors share the same Firebase project; no flavor-specific config needed |
| Utility-first | ✅ Pass | No new utility helpers needed; all logic fits inline in `LoginActivity` |
| No automated tests | ✅ Pass | No tests to write or maintain |
| Complexity gate | ✅ Pass | Net change: ~30 lines of Kotlin + 1 dependency line |

**No constitution violations. Approved to proceed.**

## Project Structure

### Documentation (this feature)

```text
specs/005-login-version-notification/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0 decisions
├── data-model.md        # Phase 1 entity/logic model
└── checklists/
    └── requirements.md  # Spec quality checklist
```

### Source Code (files touched)

```text
app/
├── build.gradle                                          # +1 line: firebase-config dependency
└── src/main/java/com/inspection/
    └── LoginActivity.kt                                  # +~30 lines: checkRemoteConfigVersion()
```

## Implementation Phases

### Phase 1 — Dependency

**File**: `app/build.gradle`

Add after the existing Firebase dependencies (lines 130–131):

```groovy
implementation 'com.google.firebase:firebase-config'
```

No BOM version bump. No `google-services.json` changes needed — Firebase is already initialized.

---

### Phase 2 — Remote Config check in LoginActivity

**File**: `app/src/main/java/com/inspection/LoginActivity.kt`

#### 2a. Imports to add

```kotlin
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
```

#### 2b. New private function

Add `checkRemoteConfigVersion()` as a private function in `LoginActivity`:

```kotlin
private fun checkRemoteConfigVersion() {
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    val settings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(3600)
        .build()
    remoteConfig.setConfigSettingsAsync(settings)
    remoteConfig.setDefaultsAsync(mapOf(
        "latest_version_code" to 0L,
        "latest_version_name" to ""
    ))
    remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
        if (!task.isSuccessful) return@addOnCompleteListener
        val remoteVersionCode = remoteConfig.getLong("latest_version_code").toInt()
        if (remoteVersionCode > BuildConfig.VERSION_CODE) {
            val remoteVersionName = remoteConfig.getString("latest_version_name")
            showUpdateAvailableDialog(remoteVersionName)
        }
    }
}

private fun showUpdateAvailableDialog(versionName: String) {
    val message = if (versionName.isNotBlank())
        "Version $versionName is now available. Please update the app to get the latest features and improvements."
    else
        "A new version of the app is available. Please update to get the latest features and improvements."
    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
    builder.setTitle("Update Available")
    builder.setMessage(message)
    builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
    builder.setCancelable(true)
    builder.show()
}
```

#### 2c. Call site

Override `onResume()` in `LoginActivity` and call `checkRemoteConfigVersion()` there. `FirebaseApp.initializeApp(this)` stays in `onCreate()` (already present, untouched). Do **not** add the call to `onCreate()` — that would miss background-resume scenarios required by the spec edge case.

```kotlin
override fun onResume() {
    super.onResume()
    checkRemoteConfigVersion()
}
```

**Why `onResume()` not `onCreate()`**: Android does not recreate `LoginActivity` when the user backgrounds and resumes the app without the process being killed. `onResume()` fires on every foreground transition, ensuring the dialog condition is re-evaluated each time the login screen becomes active — including after a background-and-reopen. Firebase RC's `minimumFetchIntervalInSeconds(3600)` cache prevents redundant network calls; the listener fires from the cached value.

---

### Phase 3 — Firebase Remote Config console setup

There is **one Firebase project** containing two registered Android apps:
- **UAT bundle** — package `com.inspection.uat`
- **Production bundle** — package `com.inspection`

Firebase Remote Config supports **Conditions** that target a specific registered app (by Firebase App ID). Use this to set independent values per bundle without a second project.

**Steps in Firebase Console → Remote Config:**

1. Create a condition named `UAT bundle`:
   - Rule: **App** → select the `com.inspection.uat` Firebase App ID

2. Add parameter `latest_version_code` (integer):
   - Default value (Production): `131` — no dialog shown until a real update ships
   - Value for condition `UAT bundle`: `132` — triggers the dialog for UAT testing

3. Add parameter `latest_version_name` (string):
   - Default value (Production): *(empty string)* — fallback message used if blank
   - Value for condition `UAT bundle`: e.g. `3.0.33` — shown in the dialog message

4. Publish changes.

| Bundle | Package | `latest_version_code` | `latest_version_name` | Dialog shown? |
|--------|---------|----------------------|----------------------|---------------|
| UAT | `com.inspection.uat` | `132` | `3.0.33` | Yes — "Version 3.0.33 is now available…" |
| Production | `com.inspection` | `131` | *(empty)* | No — until bumped for a real release |

Default in-app fallbacks (when key is absent or RC unreachable): `latest_version_code = 0`, `latest_version_name = ""` — set via `setDefaultsAsync`, ensures no dialog on first install or network failure. If `latest_version_name` is blank, the dialog message falls back to "A new version of the app is available…".

---

## Acceptance Verification

| Scenario | Expected Result |
|----------|----------------|
| `latest_version_code` > `BuildConfig.VERSION_CODE` | Dialog appears within 3s of login screen loading |
| `latest_version_code` == `BuildConfig.VERSION_CODE` | No dialog; login screen normal |
| `latest_version_code` < `BuildConfig.VERSION_CODE` | No dialog; login screen normal |
| Remote Config unreachable (airplane mode) | No dialog; login screen fully accessible |
| Tap "Dismiss" | Dialog closes; login form accessible |
| Tap "Update Now" | Opens Play Store listing for the app |
| User logs in, logs out, returns to login | Dialog reappears if condition still holds |
