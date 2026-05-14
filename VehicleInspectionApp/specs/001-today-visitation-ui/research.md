# Research: Today Visitation Screen UI Modernization

**Date**: 2026-05-15
**Feature**: `specs/001-today-visitation-ui/`

---

## Decision 1: Material Design Component Strategy

**Decision**: Use Material Design components already in `build.gradle` (`material:1.13.0`).
No new dependencies required.

**Rationale**: `MaterialCardView`, `BottomSheetDialogFragment`, `MaterialButton`, and chip-style
`TextView` with shape drawable backgrounds are all available from the existing Material library.
Adding a separate chip or card library would violate the constitution's no-new-libraries
constraint and increase APK size unnecessarily.

**Alternatives considered**:
- Custom `ViewGroup` subclasses for stat chips → rejected (unnecessary complexity; Material
  provides equivalent via `MaterialCardView` + styled `TextView`)
- Jetpack Compose for the header only → rejected (constitutionally prohibited; no Compose
  dependency exists)

---

## Decision 2: Status Badge Implementation

**Decision**: Implement status badges as `TextView` widgets with custom shape drawable
backgrounds (`badge_planned.xml` / `badge_completed.xml`), set programmatically in
`onBindViewHolder`.

**Rationale**: The existing codebase already uses this pattern (`badge_saved_bg.xml`,
`badge_unsaved_bg.xml`, `rounded_green_bkg.xml` all present in `res/drawable/`). Following
the established pattern avoids introducing Material `Chip` widgets, which carry more state
complexity than needed for a read-only badge.

**Color mapping**:
- `"Planned"` (or empty): `@color/mainColor` (#4572ce) background, white text
- `"Completed"`: `@color/green` (#49C566) background, white text

**Alternatives considered**:
- Material `Chip` widget → rejected (read-only chips add unnecessary padding/state overhead)
- Color tinting the entire card border → kept as a secondary complement (card tint to
  `@color/light_green` for Completed), not as the sole indicator

---

## Decision 3: ETA Three-State Management

**Decision**: Use the existing `etaLabel: String` field in `TodayVisitationModel` with
three sentinel values:
- `""` (empty): location not yet resolved — hide ETA row
- `"Calculating route…"`: written before the `getETAUsingRoutesAPI` call — show loading text
- Any other string: actual ETA result — show as-is

**Rationale**: Adding a new field (e.g., `enum EtaState`) to `TodayVisitationModel` would
require updating `saveTodayVisitations` / `getTodayVisitations` serialization in Utils.kt.
The string sentinel approach requires zero model changes.

**Where to write the loading state**: In `addMyLocationMarker()` in
`TodayVisitationFragment.kt`, before the `getETAUsingRoutesAPI` loop, set
`visit.etaLabel = "Calculating route…"` for each visit with a non-zero location and an
empty current `etaLabel`.

**Alternatives considered**:
- New `EtaState` enum field → rejected (requires model + serialization changes)
- ProgressBar in ETA row → rejected (spec Q4 answer chose static text)

---

## Decision 4: Clear Confirmation Dialog

**Decision**: Reuse the `AlertDialog.Builder` + `decision_dialog.xml` inflate pattern
already used in `handleCancelButtonClick()` (TodayVisitationFragment.kt:3118).

**Rationale**: `decision_dialog.xml` provides a styled, branded confirmation dialog consistent
with the rest of the app. Using it avoids introducing a new dialog layout and keeps the
destructive-action confirmation visually consistent with existing patterns.

**Message text**: "This will remove all planned visits for today. Continue?"
**Confirm button**: "Clear All"
**Cancel button**: "Cancel"

**Alternatives considered**:
- Material `MaterialAlertDialogBuilder` → acceptable but the existing custom layout is
  already branded and consistent; no benefit to switching
- Undo snackbar (spec option C) → rejected by user in Q2

---

## Decision 5: Voice Note Dialog Migration

**Decision**: Extract voice-note logic into `VoiceNoteBottomSheet` — a new
`BottomSheetDialogFragment` in the same `fragments/` package. The `showVoiceDialog()`
method in `TodayVisitationFragment` is replaced with `VoiceNoteBottomSheet.show(...)`.

**Rationale**: `BottomSheetDialogFragment` from Material 1.13.0 provides the modern
bottom-sheet presentation without custom window dimension code. The speech recognition
logic (SpeechRecognizer, hold-to-talk touch handler, Lottie wave animation) is moved
verbatim — no logic changes, only the container changes.

**Layout**: New `bottom_sheet_voice_input.xml` mirrors `dialog_voice_input.xml` structure
but wraps content in a `LinearLayout` with `paddingBottom="24dp"` for the bottom-sheet
handle area.

**Alternatives considered**:
- Keep `AlertDialog` but restyle → rejected (doesn't meet spec FR-008's bottom-sheet requirement)
- `BottomSheetBehavior` on a persistent layout → rejected (overkill for a transient dialog)

---

## Decision 6: Empty State Implementation

**Decision**: Add a dedicated empty-state `LinearLayout` to `fragment_today_visitation.xml`,
containing a `LottieAnimationView` (reuse existing Lottie integration) and a `TextView`
("No visits planned for today"). Toggled via `visibility` in `loadDataAndMap()`.

**Rationale**: Lottie is already integrated (version 6.7.1). A simple animation + message
is the standard Material empty-state pattern and requires no new assets beyond selecting
an appropriate Lottie JSON file from the app's existing Lottie assets.

**Trigger**: `if (visitsList.isEmpty()) showEmptyState() else hideEmptyState()` called at
the end of `loadDataAndMap()`.

**Alternatives considered**:
- Static drawable image → less engaging; Lottie already available
- Custom `EmptyStateView` class → unnecessary; a single `LinearLayout` suffices

---

## Decision 7: Move Button Icons

**Decision**: Create four new vector drawable XML files using standard Material arrow icons:
- `ic_move_first.xml`: `keyboard_double_arrow_up` path
- `ic_move_up.xml`: `keyboard_arrow_up` path
- `ic_move_down.xml`: `keyboard_arrow_down` path
- `ic_move_last.xml`: `keyboard_double_arrow_down` path

**Rationale**: AndroidX Material icon paths are freely available as vector XML and require
no additional asset downloads. This is consistent with how other icon drawables are defined
in `res/drawable/`.

**Content descriptions** (FR-004 / SC-006):
- `ic_move_first`: `"Move to first position"`
- `ic_move_up`: `"Move up one position"`
- `ic_move_down`: `"Move down one position"`
- `ic_move_last`: `"Move to last position"`

**Touch target**: `minWidth="48dp"` and `minHeight="48dp"` on each `ImageButton`.

**Alternatives considered**:
- Text labels on all 4 move buttons → rejected (Q1 answer: icon-only for move row)
- System drawables (`android.R.drawable.*`) → insufficient coverage for double-arrow icons
