# Tasks: Login Screen Version Update Notification

**Input**: Design documents from `/specs/005-login-version-notification/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files or external systems, no dependencies on incomplete tasks)
- **[Story]**: Which user story this task belongs to (US1, US2)
- Exact file paths included in all descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Add Firebase Remote Config SDK dependency — required before any implementation can compile.

- [x] T001 Add `implementation 'com.google.firebase:firebase-config'` to `app/build.gradle` after the existing Firebase dependencies (lines 130–131); sync Gradle

**Checkpoint**: Project compiles with Remote Config classes available

---

## Phase 2: User Story 1 — See Update Notification on Login (Priority: P1) 🎯 MVP

**Goal**: When `latest_version_code` in Firebase Remote Config is greater than the installed `BuildConfig.VERSION_CODE` (131), show an informational dialog over the login form. Dialog reappears every time the login screen is active — including after background-and-resume.

**Independent Test**: After T005 is complete, install the UAT build on a device — dialog must appear on the login screen. Dismiss it, press Home, reopen the app → dialog must appear again. Install Production build → no dialog.

### Implementation for User Story 1

- [x] T002 [US1] Implement private function `checkRemoteConfigVersion()` in `app/src/main/java/com/inspection/LoginActivity.kt`, including required imports (`FirebaseRemoteConfig`, `FirebaseRemoteConfigSettings`) — create RC instance, set `setMinimumFetchIntervalInSeconds(3600)`, set default `"latest_version_code" to 0L` via `setDefaultsAsync`, call `fetchAndActivate()`; in `addOnCompleteListener` return early if `!task.isSuccessful`, otherwise compare `remoteConfig.getLong("latest_version_code").toInt()` against `BuildConfig.VERSION_CODE` and call `showUpdateAvailableDialog()` when remote > installed
- [x] T003 [US1] Implement private function `showUpdateAvailableDialog()` in `app/src/main/java/com/inspection/LoginActivity.kt` using `androidx.appcompat.app.AlertDialog.Builder(this)` — title "Update Available", message "A new version of the app is available. Please update for the latest features and improvements.", positive button "Update Now" (start `Intent(ACTION_VIEW, Uri.parse("market://details?id=$packageName"))` catching `ActivityNotFoundException` with fallback to `https://play.google.com/store/apps/details?id=$packageName`), negative button "Dismiss" (`dialog.dismiss()`), `setCancelable(true)`
- [x] T004 [US1] Override `onResume()` in `app/src/main/java/com/inspection/LoginActivity.kt` and call `checkRemoteConfigVersion()` from it — do NOT add the call to `onCreate()`; `onResume()` ensures the check fires on background-resume scenarios as required by spec edge cases
- [ ] T005 [P] [US1] Configure Remote Config in the single Firebase project (one project, two bundles): (1) create Condition "UAT bundle" → rule **App = `com.inspection.uat`**; (2) add integer parameter `latest_version_code` — default `131` (Production), conditional `132` for "UAT bundle"; (3) add string parameter `latest_version_name` — default *(empty)* (Production), conditional `3.0.33` for "UAT bundle"; (4) publish changes — UAT build will show "Version 3.0.33 is now available…"; Production build will show no dialog

**Checkpoint**: UAT build shows dialog on login; Production build shows no dialog; dialog reappears after background-and-resume; login screen never blocked on failure/timeout

---

## Phase 3: User Story 2 — Dismiss or Act on the Notification (Priority: P2)

**Goal**: Manually validate the update dialog's two action paths — "Update Now" navigates to the Play Store listing; "Dismiss" closes the dialog and restores full login access.

**Independent Test**: With UAT build and T005 configured, trigger the dialog — tap "Update Now" → Play Store opens on the correct app listing; trigger again, tap "Dismiss" → dialog closes and login form is immediately accessible.

### Implementation for User Story 2

- [ ] T006 [US2] Manual verification on device with UAT build: (a) tap "Update Now" — confirm Play Store opens on the correct app listing for `com.inspection`; (b) tap "Dismiss" — confirm dialog closes and login fields are immediately interactive; (c) background and reopen without killing the process — confirm dialog reappears

**Checkpoint**: Both dialog actions work correctly; production bundle unaffected (no dialog via RC condition)

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Validate edge cases, timing requirement, and confirm no regression in existing post-login flow.

- [ ] T007 Build `uatDebug` variant and run full smoke-test on a device: (a) airplane mode → login screen loads normally with no dialog and no error; (b) UAT build with `latest_version_code=132` → dialog appears within approximately 3 seconds of login screen becoming interactive *(SC-001 threshold)*; (c) dismiss dialog → login form immediately accessible; (d) "Update Now" → Play Store opens on app listing; (e) background and reopen without killing process → dialog reappears (validates `onResume()` placement)
- [ ] T008 Verify the existing post-login `getAppVersion()` flow (called inside the authenticate callback in `app/src/main/java/com/inspection/LoginActivity.kt`) is unaffected — both pre-login RC check and post-login backend check must coexist independently

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **US1 (Phase 2)**: Requires T001 (Gradle sync); T002 → T003 → T004 sequential; T005 is a Firebase console task — independent of code changes, can run in parallel with T002–T004
- **US2 (Phase 3)**: T006 requires T002–T005 complete (needs a working build and RC configured to trigger the dialog)
- **Polish (Phase 4)**: Requires all prior phases complete

### Within User Story 1

- T002 → T003 → T004 (sequential: implement functions before wiring call site)
- T005 can start any time after T001 (Firebase console — no code dependency)

### Parallel Opportunities

- T005 (Firebase console config — both bundles via conditions) is an external task that can run in parallel with code tasks T002–T004

---

## Parallel Example: User Story 1

```
# Sequential code tasks:
T002 → Implement checkRemoteConfigVersion() + imports
T003 → Implement showUpdateAvailableDialog()
T004 → Wire call in onResume()

# Runs in parallel with any of the above:
T005 → Configure Firebase Remote Config (single project, both bundles via conditions)
```

---

## Implementation Strategy

### MVP (User Story 1 only)

1. T001 — Gradle dependency
2. T002 → T003 → T004 — implement functions, wire onResume()
3. T005 — configure Firebase Remote Config console (single project, UAT condition)
4. **STOP and VALIDATE**: UAT build → dialog appears; dismiss → works; background + reopen → dialog reappears; Production build → no dialog
5. Continue to T006 (US2 validation) then T007–T008 (Polish)

### Full Delivery

1. Setup (T001) → US1 code (T002–T004) + RC config (T005 in parallel) → US2 validation (T006) → Polish (T007–T008)

---

## Notes

- [P] tasks = external systems or different files — no code conflicts with sequential tasks
- One Firebase project serves both bundles — use Remote Config Conditions (App = bundle ID) to set `latest_version_code=132` for `com.inspection.uat` and `131` for `com.inspection` (Production)
- No automated tests in this project — manual validation via T006 and T007 covers acceptance scenarios
- `BuildConfig.VERSION_CODE` is currently `131` — UAT RC condition value `132` triggers the dialog; Production default `131` suppresses it
- `onResume()` placement is intentional: `onCreate()` would miss background-resume scenarios (spec edge case); Firebase RC caching prevents network redundancy
- The existing `getAppVersion()` check (post-login, OkHttp, backend API) must remain untouched — this feature is pre-login only
