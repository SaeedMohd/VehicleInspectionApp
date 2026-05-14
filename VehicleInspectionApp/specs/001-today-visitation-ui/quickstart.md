# Quickstart: Manual Validation Guide

**Feature**: Today Visitation Screen UI Modernization
**Date**: 2026-05-15

## Prerequisites

- Android tablet (or emulator) in **landscape orientation**
- UAT debug build: `./gradlew assembleUatDebug`
- At least 2–3 planned visits saved from a previous session (or use the in-app Add flow)

---

## Build & Install

```bash
# From repo root
./gradlew assembleUatDebug
# Install via adb
adb install app/build/outputs/apk/uat/debug/app-uat-debug.apk
```

---

## Validation Checklist

### 1. Header — Stat Chips

- [ ] Launch app → navigate to Today Visitation screen
- [ ] Confirm **Planned** chip shows the correct count in `@color/mainColor` (blue)
- [ ] Confirm **Completed** chip shows the correct count in `@color/green`
- [ ] Confirm **Reload** and **Clear** buttons are visible without scrolling
- [ ] Tap **Reload** → loading indicator appears briefly → counts refresh

### 2. Clear Confirmation Dialog

- [ ] Tap **Clear**
- [ ] Confirm a dialog appears: "This will remove all planned visits for today. Continue?"
- [ ] Tap **Cancel** → list unchanged, counts unchanged
- [ ] Tap **Clear** again → tap **Clear All** → list empties, Planned count resets to 0

### 3. Empty State

- [ ] After clearing, confirm the list pane shows an empty-state animation/message
  ("No visits planned for today") — NOT a blank white area
- [ ] Confirm empty state appears within 1 second

### 4. Card — Status Badge

- [ ] Add at least one visit that is in "Planned" state
- [ ] Confirm a blue "Planned" badge is visible on the card
- [ ] Complete a visit (via Load Facility → complete the inspection form)
- [ ] Return to Today Visitation → confirm the card now shows a green "Completed" badge
- [ ] Confirm the Completed card has a light-green background tint

### 5. Card — Action Buttons (Two-Row Layout)

- [ ] Confirm **primary row**: Save Notes, Remove, Load Facility — icon + label buttons
- [ ] Confirm **secondary row**: 4 icon-only arrow buttons (First, Up, Down, Last)
- [ ] Tap each button and confirm correct behavior:
  - Save Notes → notes saved (verify by closing and reopening)
  - Remove → card removed from list
  - Load Facility → facility data loads into forms
  - Move First / Up / Down / Last → card repositions correctly
- [ ] Long-press a card and drag → confirm drag-to-reorder works

### 6. Icon Button Accessibility

- [ ] Enable TalkBack on the device
- [ ] Focus each Move button with TalkBack
- [ ] Confirm announcements: "Move to first position", "Move up one position",
  "Move down one position", "Move to last position"
- [ ] Confirm each button's touch target is comfortable to tap (≥ 48dp)

### 7. ETA States

- [ ] With a visit that has no geocoordinates: confirm ETA row is hidden (not blank text)
- [ ] Grant location permission → tap Reload → confirm ETA row shows "Calculating route…"
  briefly while the Routes API call is in flight
- [ ] After calculation: confirm ETA row shows "ETA: X min · Y km (Z miles)"
- [ ] With no available route (e.g., unreachable location): confirm "ETA unavailable"

### 8. Voice Note Bottom Sheet

- [ ] Tap the mic icon on any card
- [ ] Confirm a **bottom sheet** slides up (not a centered dialog)
- [ ] Hold the button → speak → confirm transcribed text appears
- [ ] Release → confirm transcription stops
- [ ] Tap Save → confirm note saves and sheet dismisses
- [ ] Tap mic again → tap Close (X) → confirm sheet dismisses with no change

### 9. Map Visual Coherence

- [ ] With visits that have resolved geocoordinates, confirm:
  - Numbered map markers match the order circles on their respective cards
  - Map container has rounded corners matching the card style
  - No jarring color contrast between list pane and map pane

### 10. Landscape Regression

- [ ] All validations above performed in **landscape orientation**
- [ ] Rotate to portrait → confirm no layout crashes or text truncation
- [ ] Return to landscape → confirm layout restores correctly

### 11. Color Audit (SC-004)

- [ ] With the updated screen open, identify every color used across header, cards, and map container
- [ ] Confirm no more than **2 primary colors** (expected: `@color/mainColor` blue + `@color/green`) plus neutral tones (`@color/white`, `@color/light_gray`, `@color/gray`)
- [ ] Confirm status badge colors (`mainColor` for Planned, `green` for Completed) do not introduce a third distinct primary color
- [ ] If a third primary color is found, flag for design review before shipping

---

## Known Constraints

- The voice note bottom sheet requires `RECORD_AUDIO` permission. If denied, the mic
  button should gracefully do nothing (existing behavior, not in scope to change).
- ETA calculation requires an active internet connection and valid Google Maps API key
  in the UAT flavor build.
- Drag-to-reorder requires `ItemTouchHelper` to be attached — this is wired in
  `loadDataAndMap()` and should persist across reloads.
