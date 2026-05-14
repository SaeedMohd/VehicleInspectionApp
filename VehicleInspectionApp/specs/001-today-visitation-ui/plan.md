# Implementation Plan: Today Visitation Screen UI Modernization

**Branch**: `001-today-visitation-ui` | **Date**: 2026-05-15 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-today-visitation-ui/spec.md`

## Summary

Modernize the Today Visitation screen's visual design by replacing the raw `TableLayout`
header with Material stat chips, upgrading each facility card to use a status badge and a
two-row action button layout, adding an empty-state view, defining three ETA visual states,
replacing the voice-note `AlertDialog` with a `BottomSheetDialogFragment`, and wiring a
confirmation dialog into the Clear action. No new dependencies, no new API calls, and no
changes to state management patterns are required.

## Technical Context

**Language/Version**: Kotlin 2.2.21 / Java 11 (mixed codebase; new code in Kotlin only)

**Primary Dependencies**: Material Design 1.13.0 (already imported), AndroidX ConstraintLayout,
Lottie 6.7.1, Google Maps SDK 20.0.0, Volley 1.2.1, OkHttp 5.3.2

**Storage**: SharedPreferences via `getTodayVisitations` / `saveTodayVisitations` in
`Utils/Utils.kt` — no changes to storage pattern

**Testing**: None (Constitution Principle V)

**Target Platform**: Android API 24–34, landscape tablet (primary), portrait (bonus)

**Project Type**: Android mobile app — single-screen UI enhancement

**Performance Goals**: Empty state visible within 1 second of screen load (SC-003);
no new network calls introduced

**Constraints**: Landscape orientation primary; no new libraries; existing `@color/*`
tokens reused; no ViewModel/LiveData; no new API endpoints

**Scale/Scope**: 2 layout files modified, 1 new layout file, 1 new Kotlin class,
~4 new drawable resources, 1 existing Kotlin file (TodayVisitationFragment.kt) modified

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|---|---|---|
| I. Fragment-Based Architecture | ✅ PASS | TodayVisitationFragment already exists in MainActivity; modification only, no new navigation |
| II. Singleton State Management | ✅ PASS | No ViewModel/LiveData introduced; SharedPreferences pattern via Utils.kt unchanged |
| III. Flavor-Aware, No Hardcoded Endpoints | ✅ PASS | No new API endpoints; Routes API key via `R.string.GoogleMapPlaces_Key` |
| IV. Utility-First — No Duplication | ✅ PASS | Utils.kt checked; no new utility functions needed; only layout XML and adapter binding changes |
| V. No Automated Test Infrastructure | ✅ PASS | No test tasks in scope |
| Technology Stack | ✅ PASS | Material 1.13.0 already in build.gradle; BottomSheetDialogFragment from same library |

*Post-design re-check: All gates pass. No violations.*

## Project Structure

### Documentation (this feature)

```text
specs/001-today-visitation-ui/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit-tasks — NOT created here)
```

### Source Code (repository root)

```text
app/src/main/
├── java/com/inspection/fragments/
│   ├── TodayVisitationFragment.kt          # modify: header binding, Clear dialog,
│   │                                       #   ETA state writes, empty-state toggle,
│   │                                       #   PlacesAdapter (inner class, same file)
│   └── VoiceNoteBottomSheet.kt             # new: BottomSheetDialogFragment replacing
│                                           #   the AlertDialog voice-note dialog
├── res/
│   ├── layout/
│   │   ├── fragment_today_visitation.xml   # modify: replace TableLayout header with
│   │   │                                   #   ConstraintLayout + MaterialCardView chips;
│   │   │                                   #   add empty-state view
│   │   ├── today_visit_item.xml            # modify: add status badge, restructure action
│   │   │                                   #   buttons into primary row + icon-only row
│   │   └── bottom_sheet_voice_input.xml    # new: bottom-sheet layout for voice dialog
│   └── drawable/
│       ├── badge_planned.xml               # new: rounded rect, mainColor fill
│       ├── badge_completed.xml             # new: rounded rect, green fill
│       ├── ic_move_first.xml               # new: vector — double-up arrow
│       ├── ic_move_up.xml                  # new: vector — single-up arrow
│       ├── ic_move_down.xml                # new: vector — single-down arrow
│       └── ic_move_last.xml                # new: vector — double-down arrow
```

**Structure Decision**: Android single-project layout. All changes are within the existing
`app/` module. `PlacesAdapter` remains as an inner class of `TodayVisitationFragment.kt`
(consistent with the existing pattern — moving it would be out of scope).

## Complexity Tracking

> No constitution violations — table not required.

---

## Phase 0: Research Findings

See [research.md](research.md) for full findings. Summary:

- **Material components**: `MaterialCardView`, `BottomSheetDialogFragment` available from
  Material 1.13.0 already in build.gradle. No dependency changes required.
- **Status badge pattern**: Existing `badge_saved_bg.xml` / `rounded_green_bkg.xml`
  confirm the shape drawable approach. Two new drawables (`badge_planned.xml`,
  `badge_completed.xml`) follow the same pattern.
- **Clear confirmation**: Reuse the `decision_dialog.xml` + `AlertDialog.Builder` pattern
  already used in `handleCancelButtonClick()` (TodayVisitationFragment.kt:3118).
- **ETA loading state**: Write `"Calculating route…"` to `visit.etaLabel` immediately
  before dispatching the Routes API call; the existing `etaLabel` binding in
  `onBindViewHolder` will display it automatically.
- **Empty state**: Add a `LinearLayout` with a `TextView`/`LottieAnimationView` to
  `fragment_today_visitation.xml`; toggle visibility in `loadDataAndMap()` based on
  `visitsList.isEmpty()`.
- **Voice dialog migration**: `BottomSheetDialogFragment` replaces the `AlertDialog`
  in `showVoiceDialog()`; all speech recognition logic moves intact; only the
  container and layout change.
- **Icon vectors**: Use `@drawable/ic_*` AndroidX Material standard arrows (available
  via `res/drawable/` XML vector assets); four new vector XML files needed.

---

## Phase 1: Design Artifacts

See [data-model.md](data-model.md) and [quickstart.md](quickstart.md).

### Key Design Decisions

#### Header Layout

Replace the `TableLayout` in `fragment_today_visitation.xml` with a `ConstraintLayout`
inside a `MaterialCardView`. Layout:

```
[ 📋 Planned  N ]  [ ✅ Completed  N ]  [ Reload ]  [ Clear ]
```

- Stat chips: `TextView` with `@drawable/badge_planned` / `@drawable/badge_completed`
  background; label + count in a vertical `LinearLayout` inside a `MaterialCardView`
  with `cardElevation="4dp"`, `cardCornerRadius="8dp"`.
- Reload / Clear: `MaterialButton` with `style="@style/Widget.Material3.Button.TonalButton"`.

#### Card Item Layout (`today_visit_item.xml`)

Revised structure within the existing `ConstraintLayout`:

```
[ Order Circle ]  [ Facility Name (bold, mainColor) ]  [ Status Badge ]
[ Left col: Fac#, City, Type ]  [ Right col: Club, Annual Month ]
[ ETA row (always present, text changes per state) ]
[ Notes EditText + mic icon ]
[ PRIMARY ROW: Save Notes | Remove | Load Facility ]     ← icon + label
[ MOVE ROW:  ⏫ First | ↑ Up | ↓ Down | ⏬ Last ]        ← icon-only, 48dp min
```

- Status badge: `TextView` with `@drawable/badge_planned` or `@drawable/badge_completed`
  background, set in `onBindViewHolder` based on `status.equals("Completed", ignoreCase=true)`.
- ETA row: always `visibility="VISIBLE"`; text set to `"Calculating route…"` /
  `"ETA unavailable"` / actual ETA string per state.
- Move buttons: `ImageButton` with `android:contentDescription`, `minWidth="48dp"`,
  `minHeight="48dp"`.

#### Completed Card Visual Treatment

When `status == "Completed"`: set `cardRL` background tint to `@color/light_green`
(#d9ead3, already in colors.xml).

#### Voice Note Bottom Sheet (`VoiceNoteBottomSheet.kt`)

`BottomSheetDialogFragment` subclass. Exposes a `fun show(fm, onSave: (String) -> Unit)`
factory. Internal layout (`bottom_sheet_voice_input.xml`) mirrors current
`dialog_voice_input.xml` structure; speech recognizer logic moved verbatim.
`showVoiceDialog()` in `TodayVisitationFragment` replaced with a call to
`VoiceNoteBottomSheet`.
