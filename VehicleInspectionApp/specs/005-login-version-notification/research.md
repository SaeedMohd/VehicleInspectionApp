# Research: Login Screen Version Update Notification

**Feature**: `005-login-version-notification`
**Date**: 2026-06-10

## Decision 1 — Firebase Remote Config SDK

**Decision**: Add `firebase-config` dependency via the existing Firebase BOM.

**Rationale**: The project already uses Firebase BOM 34.9.0 for Crashlytics and Analytics. Adding `implementation 'com.google.firebase:firebase-config'` requires no BOM version bump and no new Firebase app configuration — `FirebaseApp.initializeApp(this)` is already called in `LoginActivity.onCreate()`.

**Alternatives considered**: Backend API polling (like the existing `getAppVersion()` endpoint at `Constants.getAppVersion`). Rejected because it requires a network round-trip to our own server, adds latency, and has no offline caching. Firebase Remote Config caches values locally for up to 12 hours by default, making it resilient to network failures.

---

## Decision 2 — Where to call Remote Config fetch

**Decision**: Call Remote Config fetch inside `LoginActivity.onCreate()`, immediately after `FirebaseApp.initializeApp(this)`.

**Rationale**: The fetch is non-blocking (async). Calling it in `onCreate` gives the maximum time for the result to arrive before the user taps Login. The fetch result is evaluated and the dialog shown only after the Remote Config `fetchAndActivate()` completes — if it times out or fails, no dialog is shown and the login screen remains fully accessible.

**Alternatives considered**: Calling in `onStart` or `onResume` (would re-fetch on every back-navigation to login screen, which is correct per spec but would cause redundant fetches on orientation change). `onCreate` is the right entry point since the Activity is recreated on each fresh launch.

---

## Decision 3 — Version comparison field

**Decision**: Compare `BuildConfig.VERSION_CODE` (integer, currently `131`) against the integer value of the `latest_version_code` Remote Config key.

**Rationale**: `BuildConfig.VERSION_CODE` is a compile-time constant, always available, requires no PackageManager lookup, and is a monotonically increasing integer — ideal for simple `<` comparison. The existing `getAppVersion()` uses version name strings (`R.string.app_version`), which require string parsing. Integer comparison is simpler and crash-proof.

**Alternatives considered**: Using `BuildConfig.VERSION_NAME` (string "3.33") — rejected because string semantic version comparison is fragile. Using `PackageManager` to get `versionCode` — rejected because `BuildConfig.VERSION_CODE` is equivalent and simpler.

---

## Decision 4 — Relationship to existing `getAppVersion()`

**Decision**: The new Firebase Remote Config check is a separate, independent check from the existing `getAppVersion()` backend call.

**Rationale**: The existing `getAppVersion()` is called after successful login (inside the authenticate callback) and shows a different-style dialog with release notes and YES/NO options that gate further app initialization. The new check is shown *before* login, is purely informational, and is always dismissable. They serve different purposes and must not be merged.

**Alternatives considered**: Replacing `getAppVersion()` entirely — rejected as it would remove release-notes functionality and break existing post-login flow.

---

## Decision 5 — Dialog implementation

**Decision**: Use `androidx.appcompat.app.AlertDialog.Builder` matching the existing pattern already used in `LoginActivity.kt` (lines 392–405).

**Rationale**: Consistent with the existing version alert dialog pattern in the same Activity. No new UI components required.

**Alternatives considered**: Custom dialog fragment — rejected as over-engineering for a simple two-button informational dialog. Bottom sheet — rejected as it was not the selected style (dialog/popup was chosen in clarification Q1).
