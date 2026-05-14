---
description: "Task list for Today Visitation Screen UI Modernization"
---

# Tasks: Today Visitation Screen UI Modernization

**Input**: Design documents from `specs/001-today-visitation-ui/`

**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, quickstart.md ✅

**Tests**: No test tasks — no automated test infrastructure (Constitution Principle V).

**Organization**: Tasks grouped by user story for independent implementation and delivery.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1–US4)
- Paths are relative to `app/src/main/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: New drawable and vector assets needed by all user stories.

- [X] T001 [P] Create `res/drawable/badge_planned.xml` — rounded-rect shape drawable, `@color/mainColor` fill, 12dp corners, 8dp h-padding, 4dp v-padding
- [X] T002 [P] Create `res/drawable/badge_completed.xml` — rounded-rect shape drawable, `@color/green` fill, 12dp corners, 8dp h-padding, 4dp v-padding
- [X] T003 [P] Create `res/drawable/ic_move_first.xml` — vector drawable, double-up-arrow icon, 24dp × 24dp, `@color/mainColor` fill
- [X] T004 [P] Create `res/drawable/ic_move_up.xml` — vector drawable, single-up-arrow icon, 24dp × 24dp, `@color/mainColor` fill
- [X] T005 [P] Create `res/drawable/ic_move_down.xml` — vector drawable, single-down-arrow icon, 24dp × 24dp, `@color/mainColor` fill
- [X] T006 [P] Create `res/drawable/ic_move_last.xml` — vector drawable, double-down-arrow icon, 24dp × 24dp, `@color/mainColor` fill

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Layout and class scaffolding that all user story implementations depend on.

**⚠️ CRITICAL**: No user story work begins until this phase is complete.

- [X] T007 Redesign `res/layout/fragment_today_visitation.xml` — replace the `TableLayout` header with a `ConstraintLayout` inside a `MaterialCardView` (elevation 4dp, corner radius 8dp); add two stat chip `LinearLayout`s (planned, completed), two `MaterialButton`s (Reload, Clear); add a `LinearLayout` empty-state container (`android:id="@+id/emptyStateView"`, `visibility="gone"`) containing a `LottieAnimationView` with `android:id="@+id/emptyStateLottie"` and a `TextView` "No visits planned for today"; keep the existing split `LinearLayout` (list left / map right) unchanged
- [X] T008 Redesign `res/layout/today_visit_item.xml` — within the existing `ConstraintLayout`: (1) add a `TextView` `id="statusBadge"` constrained top-end; (2) keep left/right info columns; (3) update ETA row to `visibility="visible"` always; (4) replace the three `Button` primary row with three `MaterialButton`s (icon+label: Save Notes, Remove, Load Facility); (5) replace the four `Button` move row with four `ImageButton`s (`id` moveFirstBtn / moveUpBtn / moveDownBtn / moveLastBtn, `minWidth="48dp"`, `minHeight="48dp"`, src set to ic_move_* drawables, `contentDescription` set per data-model.md)
- [X] T009 Create `res/layout/bottom_sheet_voice_input.xml` — `LinearLayout` root with bottom-sheet handle area (8dp top padding, drag handle view), then mirror structure of existing `dialog_voice_input.xml`: `LottieAnimationView` `id="btnHoldToTalk"`, `LottieAnimationView` `id="lottieWave"`, `TextView` `id="tvSpeechText"`, `TextView` `id="listeningText"`, `Button` `id="dialogSaveBtn"`, `ImageView` `id="dialogCloseBtn"`
- [X] T010 Create `java/com/inspection/fragments/VoiceNoteBottomSheet.kt` — `BottomSheetDialogFragment` subclass; inflate `bottom_sheet_voice_input.xml` in `onCreateView`; add companion `fun show(fm: FragmentManager, onSave: (String) -> Unit)` factory method; wire all view references matching the existing `dialog_voice_input.xml` IDs; migrate the complete `SpeechRecognizer` / hold-to-talk / Lottie wave animation logic verbatim from `showVoiceDialog()` in `TodayVisitationFragment.kt`; call `onSave(text)` on Save tap

**Checkpoint**: Layouts compiled, `VoiceNoteBottomSheet` class present — ready for story-by-story wiring.

---

## Phase 3: User Story 1 — Modern Header Dashboard (Priority: P1) 🎯 MVP

**Goal**: Replace the `TableLayout` header with Material stat chips; wire Reload and Clear
with confirmation dialog.

**Independent Test**: Open Today Visitation screen → verify planned/completed chip counts
are correct, Reload refreshes data, Clear shows confirmation dialog and empties list on confirm.

### Implementation for User Story 1

- [X] T011 [US1] In `TodayVisitationFragment.kt` update `onViewCreated` binding references — replace any `binding.topSavedll`-scoped `TableRow`/`TableLayout` references with the new chip `TextView` ids (`plannedVal`, `completedVal`) and button ids (`reloadBtn`, `clearBtn`); no logic change needed for Reload
- [X] T012 [US1] In `TodayVisitationFragment.kt` replace the `clearBtn` click handler — wrap the existing clear logic in an `AlertDialog.Builder` using `layoutInflater.inflate(R.layout.decision_dialog, null)` (reuse pattern from `handleCancelButtonClick()`); set title "Clear Planned Visits", message "This will remove all planned visits for today. Continue?"; wire confirm button to execute clear; wire cancel button to dismiss with no action
- [X] T013 [US1] In `TodayVisitationFragment.kt` `loadDataAndMap()` — add empty-state toggle at the end of the method: if `visitsList.isEmpty()` set `binding.emptyStateView.visibility = View.VISIBLE` and `binding.listRecyclerView.visibility = View.GONE`; else reverse; start Lottie animation on `binding.emptyStateLottie` when visible

**Checkpoint**: Header chips show correct counts, Reload works, Clear requires confirmation, empty state shows when list is empty.

---

## Phase 4: User Story 2 — Modern Card Layout (Priority: P1)

**Goal**: Status badge, two-row action buttons, ETA three states, completed card background
tint, drag-to-reorder unchanged.

**Independent Test**: Card shows "Planned" / "Completed" badge with correct color; all 7
action buttons functional; ETA shows three distinct states; completed card has green tint.

### Implementation for User Story 2

- [X] T014 [US2] In `PlacesAdapter.StepViewHolder` (inside `TodayVisitationFragment.kt`) — rename `status_text` field to `statusBadge`; change type to `TextView`; update `itemView.findViewById` call to `R.id.statusBadge`; change `moveUpBtn`, `moveDownBtn`, `moveLastBtn`, `moveFirstBtn` from `Button` to `ImageButton` with correct `R.id.*` references
- [X] T015 [US2] In `PlacesAdapter.onBindViewHolder` — replace the existing `holder.status_text.text` / `setTextColor` block with: set `holder.statusBadge.text` to `"Completed"` or `"Planned"`; set background resource to `R.drawable.badge_completed` or `R.drawable.badge_planned`; set `holder.cardRL` background color to `ContextCompat.getColor(context, R.color.light_green)` for Completed or `Color.WHITE` for Planned (remove the old `Color.parseColor("#26C3AA")` text-color line)
- [X] T015b [US2] In `PlacesAdapter.onBindViewHolder` — in the `saveBtn` click listener, after calling `updateVisitationNotes(...)`, show `Toast.makeText(itemView.context, "Notes saved", Toast.LENGTH_SHORT).show()` to provide the brief confirmation required by US2 acceptance scenario 2
- [X] T016 [US2] In `PlacesAdapter.onBindViewHolder` — update ETA row logic: change from hiding the row when `etaLabel` is empty to always keeping the row visible; show `holder.eta_text.text = places[position].etaLabel` when non-empty; show `"Calculating route…"` text when `etaLabel` is the loading sentinel; hide only if `etaLabel == ""` (location not yet resolved at all)
- [X] T017 [US2] In `TodayVisitationFragment.kt` `addMyLocationMarker()` — before the `getETAUsingRoutesAPI` loop, for each `place` in `visitsList` where `place.latitude != 0.0` and `place.etaLabel.isEmpty()`, set `place.etaLabel = "Calculating route…"` and call `updateVisitationETA(requireContext(), place.facNum.toString(), place.clubCode, "Calculating route…")`; call `loadDataAndMap(true)` once after setting all sentinels to refresh the adapter before the API calls begin

**Checkpoint**: Cards show correct status badges, completed cards have green tint, ETA shows loading/unavailable/available states, all 7 action buttons remain functional.

---

## Phase 5: User Story 3 — Map and List Visual Coherence (Priority: P2)

**Goal**: Map container styled to match card corner radius and elevation; order circle color
aligned with map marker color.

**Independent Test**: Open screen with geocoded visits; confirm map container has rounded
corners; confirm numbered card order circles match map marker numbers.

### Implementation for User Story 3

- [X] T018 [US3] In `res/layout/fragment_today_visitation.xml` — wrap the `FragmentContainerView` for the map in a `MaterialCardView` (elevation 4dp, corner radius 8dp, same as the header card and list cards) with `app:cardUseCompatPadding="true"` and a 4dp margin; preserve the existing `android:id="@+id/mapFragment"` on the inner `FragmentContainerView`
- [X] T019 [US3] In `res/layout/fragment_today_visitation.xml` — update the outer split `LinearLayout` background from `@drawable/frame_solid_border` to `@color/light_gray` with 8dp padding; add a 1dp vertical `View` divider (`@color/gray`) between the list and map `LinearLayout`s to create a subtle visual separator

> **Edge case coverage**: The spec requires the map pane to show a loading indicator while the map is initializing. The existing full-screen `recordsProgressView` overlay (preserved unchanged by T007) satisfies this requirement during the initial data load. No additional per-pane map loading state is needed.

**Checkpoint**: List and map panes share visual language (rounded containers, neutral background, subtle divider); map markers match card order numbers.

---

## Phase 6: User Story 4 — Voice Note Bottom Sheet (Priority: P3)

**Goal**: Replace the centered `AlertDialog` voice-note dialog with `VoiceNoteBottomSheet`.

**Independent Test**: Tap mic button → bottom sheet slides up (not centered); hold-to-talk
works; transcribed text appears; Save saves the note; Close dismisses without saving.

### Implementation for User Story 4

- [X] T020 [US4] In `TodayVisitationFragment.kt` — replace the `showVoiceDialog()` call in `onVoiceClicked()` with `VoiceNoteBottomSheet.show(parentFragmentManager) { recognizedText -> updateVisitationNotes(requireContext(), facilityNumber, clubCode, recognizedText); loadDataAndMap(true) }`
- [X] T021 [US4] In `TodayVisitationFragment.kt` — delete the now-unused `showVoiceDialog()` method body (the `SpeechRecognizer` logic has been moved to `VoiceNoteBottomSheet.kt`); keep the method signature as a no-op stub or remove it entirely if no other callers exist (verify with grep before deleting)

**Checkpoint**: Tapping mic shows bottom sheet; all voice note functionality works as before; no `AlertDialog` is presented for voice input.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Landscape validation, final binding cleanup, and manual QA sign-off.

- [ ] T022 [P] In `res/layout/fragment_today_visitation.xml` — verify the layout renders correctly in landscape orientation by inspecting in Android Studio Layout Editor at a representative tablet size (e.g., 10" landscape); adjust constraints or weights if any element clips or overflows
- [ ] T023 [P] In `res/layout/today_visit_item.xml` — verify the card renders correctly in landscape at full list-pane width (50% of tablet screen); confirm move `ImageButton` touch targets are ≥ 48dp × 48dp in the Layout Editor
- [X] T024 Run `./gradlew assembleUatDebug` and confirm the build succeeds with zero errors; fix any ViewBinding or resource-not-found compilation errors
- [ ] T025 Install on tablet in landscape orientation; execute the full manual validation checklist in `specs/001-today-visitation-ui/quickstart.md` (10 sections); mark each section pass/fail

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — T001–T006 all parallel
- **Foundational (Phase 2)**: Depends on Phase 1 completion (T007 uses badge drawables, T008 uses ic_move_* drawables)
- **User Stories (Phase 3–6)**: All depend on Phase 2 completion
  - US1 (Phase 3) and US2 (Phase 4) are both P1 — can be worked in parallel by different developers
  - US3 (Phase 5) is independent of US1/US2
  - US4 (Phase 6) depends on T010 (VoiceNoteBottomSheet class, created in Phase 2)
- **Polish (Phase 7)**: Depends on all desired user story phases being complete

### User Story Dependencies

- **US1 (P1)**: Depends on Phase 2 (T007 layout, T009 dialog layout); independent of US2/US3/US4
- **US2 (P1)**: Depends on Phase 2 (T008 layout, T001–T006 drawables); independent of US1/US3/US4
- **US3 (P2)**: Depends on Phase 2 (T007 layout); independent of US1/US2/US4
- **US4 (P3)**: Depends on T010 (VoiceNoteBottomSheet class); independent of US1/US2/US3

### Within Each User Story

- Layouts (Phase 2) before adapter/fragment wiring (Phase 3–6)
- ViewHolder field changes (T014) before binding logic (T015, T016)
- ETA sentinel writing (T017) after adapter binding update (T015, T016)

### Parallel Opportunities

- All Phase 1 tasks (T001–T006): fully parallel
- T007, T008, T009, T010 in Phase 2: T007 + T008 + T009 parallel; T010 depends on T009
- US1 (T011–T013) and US2 (T014–T017) can run in parallel after Phase 2
- T018 and T019 in US3 MUST run sequentially (both modify `fragment_today_visitation.xml`)
- T022 and T023 in Polish can run in parallel

---

## Parallel Example: Phase 1 (Drawables)

```bash
# All 6 drawable tasks launch simultaneously:
Task: "Create res/drawable/badge_planned.xml"
Task: "Create res/drawable/badge_completed.xml"
Task: "Create res/drawable/ic_move_first.xml"
Task: "Create res/drawable/ic_move_up.xml"
Task: "Create res/drawable/ic_move_down.xml"
Task: "Create res/drawable/ic_move_last.xml"
```

---

## Implementation Strategy

### MVP First (US1 + US2 Only — both P1)

1. Complete Phase 1: Setup (T001–T006)
2. Complete Phase 2: Foundational (T007–T010)
3. Complete Phase 3: User Story 1 — header + clear dialog + empty state (T011–T013)
4. Complete Phase 4: User Story 2 — card badges + buttons + ETA states (T014–T017)
5. **STOP and VALIDATE**: Install UAT debug build, run quickstart.md sections 1–7
6. Ship MVP if validated

### Incremental Delivery

1. MVP (US1 + US2) → validate → ship
2. Add US3 (map coherence, T018–T019) → validate section 9 → ship
3. Add US4 (voice bottom sheet, T020–T021) → validate section 8 → ship
4. Polish (T022–T025) before final release build

---

## Notes

- `[P]` tasks = different files, no dependencies on incomplete tasks
- `[Story]` label maps each task to its user story for traceability
- All `PlacesAdapter` changes are in `TodayVisitationFragment.kt` (inner class — same file)
- Do NOT move `PlacesAdapter` to a separate file (out of scope, risks breaking existing references)
- **FR-009** (order circles match map markers): existing behavior — `createNumberedMarker(order)` and `holder.order_text.text = order.toString()` already align these; preserved by T008 and T014; verified in quickstart.md section 9
- **FR-005** (drag-to-reorder): existing `ItemTouchHelper` wiring in `loadDataAndMap()` is preserved by T008 (layout) and T014 (ViewHolder field types unchanged for drag); verified in quickstart.md section 5
- Before adding any helper function, check `Utils/Utils.kt` per Constitution Principle IV
- Commit after each phase completion
