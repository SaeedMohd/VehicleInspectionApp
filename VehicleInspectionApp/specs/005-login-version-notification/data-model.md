# Data Model: Login Screen Version Update Notification

**Feature**: `005-login-version-notification`
**Date**: 2026-06-10

## Entities

### RemoteVersionConfig (remote-only, not persisted locally)

| Field | Type | Source | Notes |
|-------|------|--------|-------|
| `latest_version_code` | `Int` | Firebase Remote Config key | Fetched via `fetchAndActivate()`; default value `0` (no dialog shown) |

### InstalledVersion (compile-time constant)

| Field | Type | Source | Notes |
|-------|------|--------|-------|
| `versionCode` | `Int` | `BuildConfig.VERSION_CODE` | Currently `131`; monotonically increasing integer |

## Comparison Logic

```
showUpdateDialog = latest_version_code > BuildConfig.VERSION_CODE
```

- If `latest_version_code` is `0` (default / fetch failed / key missing) → `0 > 131` is false → no dialog shown.
- If `latest_version_code` is non-integer or fetch fails → treat as `0` → no dialog shown.

## State Transitions

```
LoginActivity.onCreate()
  │
  ├─ FirebaseApp.initializeApp(this)        [existing]
  │
  └─ checkRemoteConfigVersion()             [new]
        │
        ├─ fetchAndActivate() [async]
        │     ├─ SUCCESS → compare versionCodes → show dialog if needed
        │     └─ FAILURE / TIMEOUT → no dialog; login screen fully accessible
        │
        └─ dialog dismissed (either button)
              └─ login form accessible (no state persisted)
```

## No Persistence

Dismissal state is **never** persisted. Every cold start or return to `LoginActivity` triggers a fresh `fetchAndActivate()` call and re-evaluates the condition.

## Firebase Remote Config Default Values

| Key | Default | Reason |
|-----|---------|--------|
| `latest_version_code` | `0` | Ensures no dialog appears if Remote Config is unconfigured or unreachable on first run |
