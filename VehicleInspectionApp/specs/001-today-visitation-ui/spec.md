# Feature Specification: Today Visitation Screen UI Modernization

**Feature Branch**: `001-today-visitation-ui`

**Created**: 2026-05-15

**Status**: Draft

**Input**: User description: "Review TodayVisitationFragment.kt to enhance the UI and make it more modern"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Today's Visitation Dashboard at a Glance (Priority: P1)

An automotive specialist opens the Today Visitation screen and immediately sees a clean,
modern summary header showing planned vs. completed counts with clear visual hierarchy.
The header is compact and scannable, using icon-backed stat chips rather than a raw
TableLayout with mixed-size text and flat buttons.

**Why this priority**: This is the first thing the user sees every time they open the screen.
Poor visual clarity here slows down daily planning. It is the screen's anchor element.

**Independent Test**: Open the Today Visitation screen with a list of visits loaded.
Verify the header shows correct planned and completed counts, the stat chips are visually
distinct, and the Reload and Clear action buttons are easily tappable.

**Acceptance Scenarios**:

1. **Given** the screen is loaded with 5 planned visits and 2 completed, **When** the user
   views the header, **Then** they see "5" in a Planned chip and "2" in a Completed chip with
   visually distinct colors (e.g., accent for planned, green for completed).
2. **Given** the user taps "Reload", **When** the data refreshes, **Then** the counts update
   and a brief loading indicator appears while data is fetched.
3. **Given** the user taps "Clear", **When** a confirmation dialog appears and the user
   confirms, **Then** the list empties and counts reset to 0. If the user cancels, no
   change occurs.

---

### User Story 2 - View and Manage Each Visitation Card with a Modern Layout (Priority: P1)

Each facility card in the list uses a modern visual design: a status badge, clearly grouped
information rows, a collapsible notes area, and icon-based action buttons replacing the
current three-row button grid. Cards display status (Planned / Completed) visually via
color or badge, not just a plain text label.

**Why this priority**: Users interact with individual cards constantly throughout the day —
loading facilities, saving notes, reordering. This is the highest-frequency interaction area.

**Independent Test**: Add 3 visits to the list. Verify each card shows facility name
prominently, key info fields are grouped, the status badge reflects the correct state, the
notes field is present, and all actions (Save Notes, Remove, Load Facility, Move First/Up/Down/Last)
are accessible and functional.

**Acceptance Scenarios**:

1. **Given** a card for a facility with status "Completed", **When** the user views the card,
   **Then** a green "Completed" badge is visible and the card background or border reflects
   the completed state.
2. **Given** a card for a facility with status "Planned", **When** the user types notes and
   taps the save action, **Then** the notes are saved and a brief confirmation is shown.
3. **Given** a card with ETA data available, **When** the user views the card, **Then** ETA
   and distance information is displayed inline in a readable format (not hidden by default).
4. **Given** a card, **When** the user uses drag-to-reorder (long press and drag), **Then**
   the card moves to the new position and the order persists.

---

### User Story 3 - Map and List Side-by-Side with Visual Coherence (Priority: P2)

The split-pane layout (list left, map right) retains its 50/50 split but gains visual
polish: a subtle divider, consistent card corner radius and shadow, and the map panel has
a rounded container matching the card style. The overall screen background uses a neutral
surface tone that ties all elements together.

**Why this priority**: Users reference the map alongside the list constantly. Visual
coherence between the two panes reduces cognitive load.

**Independent Test**: Open the screen with visits that have geocoordinates. Verify the
list pane and map pane are visually cohesive, there is no jarring color contrast between
them, and map markers match the card's numbered order indicators.

**Acceptance Scenarios**:

1. **Given** visits with known locations, **When** the screen loads, **Then** numbered map
   markers match the order circles on each card.
2. **Given** the split layout, **When** the user views both panes, **Then** the visual
   language (corner radius, shadow, surface color) is consistent across the list and map areas.

---

### User Story 4 - Voice Note Recording with Polished Dialog (Priority: P3)

The voice input dialog (hold-to-talk) uses a modern bottom-sheet style instead of a
centered alert dialog, with clearer visual feedback for recording state, accumulated
text display, and accessible Save/Cancel controls.

**Why this priority**: Voice notes are an efficiency feature used in the field. The current
dialog is functional but visually dated.

**Independent Test**: Tap the mic button on a card. Verify the bottom sheet appears, the
hold-to-talk button is clearly labeled, recording state (wave animation) is visible, and
the recognized text displays before saving.

**Acceptance Scenarios**:

1. **Given** the user taps the mic button, **When** the voice dialog appears, **Then** it
   presents as a bottom sheet with a clear "Hold to Talk" prompt.
2. **Given** the user holds the button and speaks, **When** speech is recognized, **Then**
   the transcribed text appears in the dialog in real time.
3. **Given** text is transcribed, **When** the user taps Save, **Then** the note is saved
   to the visit and the sheet dismisses.

---

### Edge Cases

- What happens when the visits list is empty? An empty-state illustration or message MUST
  be shown instead of a blank list area.
- How does the card look when ETA is loading vs. unavailable vs. available? Three distinct
  visual states are required: (1) **Loading** — display static text "Calculating route…"
  in the ETA row while the Routes API call is in flight; (2) **Unavailable** — display
  "ETA unavailable" when the API returns no route; (3) **Available** — display the ETA
  minutes and distance string (e.g., "ETA: 12 min · 8.4 km").
- What happens when a Completed card is reordered? Move buttons MUST remain functional
  regardless of status.
- How does the screen behave when the map has not yet loaded? The map pane MUST show a
  placeholder or loading indicator.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The header summary MUST display planned visitation count and completed
  visitation count as distinct visual stat elements with contrasting colors.
- **FR-002**: The Reload and Clear action buttons MUST remain accessible in the header
  area without requiring scroll. The Clear action MUST present a confirmation dialog
  ("This will remove all planned visits. Continue?") before executing; tapping Cancel
  MUST leave the list unchanged.
- **FR-003**: Each visitation card MUST display a status badge (Planned / Completed) that
  is color-coded and immediately distinguishable without relying on text color alone —
  the badge MUST use both a distinct background color and a text label. "Planned" MUST use
  a neutral or accent color; "Completed" MUST use green.
- **FR-004**: Card action buttons MUST all remain functional and accessible with no more
  than one tap. Layout MUST follow a two-row structure: (1) a primary row with Save Notes,
  Remove, and Load Facility as icon+label buttons; (2) a secondary compact icon-only row
  with Move First, Move Up, Move Down, and Move Last. No overflow menus or expand/collapse
  toggles are permitted for these actions. All icon-only buttons in the secondary row MUST
  have descriptive content descriptions (e.g., "Move to first position") and a minimum
  touch target of 48dp × 48dp.
- **FR-005**: Cards MUST support drag-to-reorder via long-press, with the order persisting
  to local storage.
- **FR-006**: When the visits list is empty, the list pane MUST display an empty-state
  message or visual cue.
- **FR-007**: ETA and distance information MUST be visible on a card when available,
  without the user having to expand or scroll to find it.
- **FR-008**: The voice note dialog MUST retain hold-to-talk behavior and accumulated
  transcription display; visual presentation SHOULD use a bottom-sheet pattern.
- **FR-009**: Numbered order indicators on cards MUST match the numbered markers on the
  map for the same facilities.
- **FR-010**: The overall screen background, card surfaces, and map container MUST use a
  visually consistent color and elevation scheme.

### Key Entities

- **TodayVisitationModel**: Facility number, club code, name, city, visit type, status,
  order index, latitude, longitude, ETA label, notes.
- **PlacesAdapter**: RecyclerView adapter binding `TodayVisitationModel` items to
  `today_visit_item.xml` cards; handles drag callbacks and action button events.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The header summary area MUST be comprehensible in under 3 seconds without
  any user instruction — verified by user observation testing.
- **SC-002**: All existing card actions (7 buttons) MUST remain accessible with no more
  than one additional tap compared to the current layout (i.e., no actions buried more
  than one level deep).
- **SC-003**: An empty visits list MUST display a non-blank state visible within 1 second
  of the screen loading.
- **SC-004**: Visual consistency score: a designer review MUST confirm the updated screen
  uses no more than 2 primary colors plus neutrals across header, cards, and map container.
- **SC-005**: The updated UI MUST run without visual regressions on the existing landscape
  tablet orientation (the app's primary target orientation).
- **SC-006**: All icon-only action buttons MUST have content descriptions verifiable via
  accessibility inspection, and touch targets MUST measure at least 48dp × 48dp.

## Assumptions

- The app runs in landscape orientation on tablets; all layout changes MUST be tested in
  landscape. Portrait compatibility is a bonus, not a requirement.
- The existing `today_visit_item.xml` and `fragment_today_visitation.xml` are the primary
  layout files to be replaced or significantly updated; the Kotlin fragment logic should
  change only as needed to support binding updates.
- The Lottie animation library is already integrated and available for loading states or
  empty-state animations.
- No new API endpoints or data model changes are required; this is a purely visual/UX change.
- `@color/mainColor`, `@color/darkBlue`, and related existing color resources MUST be
  reused where applicable for brand consistency; new color tokens may be added for status
  badges only.
- The voice dialog's underlying speech recognition logic is NOT in scope; only its visual
  container and controls are being updated.

## Clarifications

### Session 2026-05-15

- Q: How should the 7 card action buttons be condensed/presented? → A: Two-row layout — primary row (Save Notes, Remove, Load Facility) as icon+label buttons; secondary compact icon-only row (Move First, Move Up, Move Down, Move Last). No overflow menus.
- Q: Should the "Clear" action require a confirmation dialog before clearing all visits? → A: Yes — show a confirmation dialog; clearing proceeds only on explicit user confirmation.
- Q: Should icon-only move buttons require content descriptions and minimum 48dp touch targets? → A: Yes — all icon-only buttons must have content descriptions and 48dp × 48dp minimum touch targets.
- Q: What visual treatment should the ETA row show while the route is being calculated? → A: Static text "Calculating route…" displayed in the ETA row while the API call is in flight.
- Q: What is the label for the non-completed card status? → A: "Planned" (the two card status values are "Planned" and "Completed").
