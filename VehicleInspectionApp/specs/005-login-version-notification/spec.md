# Feature Specification: Login Screen Version Update Notification

**Feature Branch**: `005-login-version-notification`

**Created**: 2026-06-10

**Status**: Draft

**Input**: User description: "Need to have a notification on login screen that a new Version is available based on a firebase dynamic config"

## Clarifications

### Session 2026-06-10

- Q: What is the notification presentation style? → A: Dialog / popup that appears over the login form.
- Q: Which version field is used for comparison? → A: Integer version code — simple integer comparison (`installedVersionCode < remoteVersionCode`).
- Q: What are the Remote Config key names, and is forced update needed? → A: Single key `latest_version_code` (integer). No forced update — notification is informational only; user can always dismiss and log in.
- Q: Should the dialog reappear after the user dismisses, logs in, then logs out? → A: Yes — the dialog reappears every time the user reaches the login screen while the condition holds.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See Update Notification on Login (Priority: P1)

A user opens the app and lands on the login screen. If the integer version code stored in Firebase Remote Config under `latest_version_code` is higher than the installed app's version code, an informational update dialog is displayed over the login form.

**Why this priority**: This is the core deliverable. Without this, no other story makes sense.

**Independent Test**: Can be fully tested by setting a higher integer value for `latest_version_code` in Firebase Remote Config and launching the app to the login screen — the update dialog must appear.

**Acceptance Scenarios**:

1. **Given** the app is open on the login screen, **When** the `latest_version_code` in Remote Config is greater than the installed app version code, **Then** an informational update dialog is shown over the login form.
2. **Given** the app is open on the login screen, **When** the installed version code is equal to or greater than `latest_version_code`, **Then** no dialog is shown and the login screen appears normally.
3. **Given** Firebase Remote Config cannot be reached (no internet, timeout), **When** the login screen loads, **Then** no dialog is shown and the login screen functions normally.

---

### User Story 2 - Dismiss or Act on the Notification (Priority: P2)

A user sees the update dialog and can either dismiss it to continue logging in, or tap "Update Now" to go to the app store.

**Why this priority**: The user must always be able to dismiss and continue — the notification is informational only.

**Independent Test**: Can be tested by triggering the dialog and verifying both the dismiss and store-redirect actions work independently.

**Acceptance Scenarios**:

1. **Given** the update dialog is displayed, **When** the user taps "Dismiss" or equivalent, **Then** the dialog is closed and the login form becomes fully accessible.
2. **Given** the update dialog is displayed, **When** the user taps "Update Now", **Then** the user is navigated to the platform app store page for the app.

---

### Edge Cases

- What happens when Firebase Remote Config fetch times out? → Treat as "no update available"; do not block login.
- What happens when `latest_version_code` in Remote Config is missing or non-integer? → Treat as "no update available"; log the error silently.
- What happens if the user dismisses the dialog, logs in, then logs out? → The dialog reappears on the login screen because dismissal is never persisted.
- What happens if the user dismisses the dialog, backgrounds the app, and reopens it? → Show the dialog again (dismissal is never persisted).
- What happens on first install with no cached Remote Config? → Use Firebase default values (no dialog shown).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The app MUST fetch the `latest_version_code` integer value from Firebase Remote Config each time the login screen is displayed.
- **FR-002**: The app MUST compare the fetched integer version code against the currently installed app integer version code.
- **FR-003**: When `latest_version_code` is greater than the installed version code, the app MUST display an informational update dialog over the login form.
- **FR-004**: The update dialog MUST include a clear message that a new version is available.
- **FR-005**: The update dialog MUST include an "Update Now" action to navigate the user to the appropriate app store listing.
- **FR-006**: The update dialog MUST always include a dismiss action that closes the dialog and restores full login access.
- **FR-007**: If the Remote Config fetch fails or `latest_version_code` is missing or non-integer, the login screen MUST function normally with no dialog shown.
- **FR-008**: The Remote Config fetch MUST NOT block or delay the display of the login screen UI.

### Key Entities

- **Remote Config Key `latest_version_code`**: Integer value representing the latest available app version code.
- **Installed App Version Code**: The integer build version code of the currently running app.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The update dialog appears within 3 seconds of the login screen loading when `latest_version_code` is higher than the installed version code.
- **SC-002**: The login screen loads and is interactive within the same time as before this feature (no regression in load time).
- **SC-003**: When Remote Config is unreachable, the login screen is fully usable within the normal load time with no error shown to the user.
- **SC-004**: Tapping "Update Now" navigates the user to the correct app store page in all cases.
- **SC-005**: The dismiss action is present and functional on the update dialog in 100% of cases.

## Assumptions

- Firebase Remote Config is already integrated in the project (SDK present); only the `latest_version_code` key needs to be added.
- Version comparison is performed using integer version codes (not version name strings).
- The dialog is shown on the login screen only (not on other screens).
- The notification is purely informational — the user can always dismiss it and proceed to log in.
- Dismissal is never persisted — the dialog reappears every time the user reaches the login screen (including after logout) as long as the condition holds.
- Both UAT and Production Firebase projects will have `latest_version_code` configured before testing.
