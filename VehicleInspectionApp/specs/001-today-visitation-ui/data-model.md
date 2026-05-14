# Data Model: Today Visitation Screen UI Modernization

**Date**: 2026-05-15

This feature makes no changes to persistent data models or API contracts.
All changes are purely visual/presentational. This document describes how
existing model fields map to the new UI elements.

---

## TodayVisitationModel

**File**: `app/src/main/java/com/inspection/model/today_visitation_model.kt`
**No fields added or removed.**

| Field | Type | Default | UI Mapping |
|---|---|---|---|
| `facName` | `String` | `""` | Card header — facility name (bold, `@color/mainColor`) |
| `facNum` | `Int` | `0` | Left column — Facility Number value |
| `clubCode` | `String` | `""` | Right column — Club Code value |
| `status` | `String` | `""` | **Status badge** — `""` or `"Planned"` → `badge_planned`; `"Completed"` → `badge_completed` |
| `type` | `VisitationTypes` | `Annual` | Left column — Type value |
| `city` | `String` | `""` | Left column — City value |
| `facAnnualMonth` | `Int` | `1` | Right column — Annual Month (formatted via `monthNoToName()`) |
| `latitude` | `Double` | `0.0` | Drives order circle color (red = located, white = not located); location row visibility |
| `longitude` | `Double` | `0.0` | Same as latitude |
| `order` | `Int` | `0` | Order circle number; must match map marker number |
| `notes` | `String` | `""` | Notes `EditText` pre-fill |
| `etaLabel` | `String` | `""` | **ETA row text** — three states (see below) |

### ETA Label State Machine

```
etaLabel == ""
  → ETA row: GONE (location not yet resolved or not available)

etaLabel == "Calculating route…"
  → ETA row: VISIBLE, text = "Calculating route…"
  → Written to model before getETAUsingRoutesAPI() call

etaLabel == "ETA unavailable"  (set when API returns no route)
  → ETA row: VISIBLE, text = "ETA unavailable"

etaLabel == "* ETA: X min\n\n* Distance: Y km (Z miles)"  (existing parseNewRoutesETA format)
  → ETA row: VISIBLE, text = formatted ETA string
```

### Status Badge Logic

```kotlin
// In PlacesAdapter.onBindViewHolder:
val isCompleted = places[position].status.equals("Completed", ignoreCase = true)
holder.statusBadge.text = if (isCompleted) "Completed" else "Planned"
holder.statusBadge.setBackgroundResource(
    if (isCompleted) R.drawable.badge_completed else R.drawable.badge_planned
)
// Completed card background tint
holder.cardRL.setBackgroundColor(
    if (isCompleted) ContextCompat.getColor(context, R.color.light_green)
    else Color.WHITE
)
```

---

## New Drawable Resources

### badge_planned.xml

```xml
<shape android:shape="rectangle">
    <corners android:radius="12dp"/>
    <solid android:color="@color/mainColor"/>
    <padding android:left="8dp" android:right="8dp"
             android:top="4dp" android:bottom="4dp"/>
</shape>
```

### badge_completed.xml

```xml
<shape android:shape="rectangle">
    <corners android:radius="12dp"/>
    <solid android:color="@color/green"/>
    <padding android:left="8dp" android:right="8dp"
             android:top="4dp" android:bottom="4dp"/>
</shape>
```

---

## Adapter Interface (unchanged)

`PlacesAdapter` interfaces are **not changed**. The existing 5 listener interfaces
(`VisitActionListener`, `FullDataListener`, `CardListener`, `OnVoiceClickListener`,
`OnVoiceStopClickListener`) and `StepViewHolder` are modified only to add/rename the
new view bindings (`statusBadge`, `moveFirstBtn` as `ImageButton`, etc.).

### ViewHolder Field Changes

| Old field | Old type | New type | Change |
|---|---|---|---|
| `status_text` | `TextView` | renamed `statusBadge` | Badge instead of plain text |
| `moveUpBtn` | `Button` | `ImageButton` | Icon-only with contentDescription |
| `moveDownBtn` | `Button` | `ImageButton` | Icon-only with contentDescription |
| `moveLastBtn` | `Button` | `ImageButton` | Icon-only with contentDescription |
| `moveFirstBtn` | `Button` | `ImageButton` | Icon-only with contentDescription |
| *(new)* | — | `emptyStateView: LinearLayout` | In fragment, not adapter |

---

## VoiceNoteBottomSheet

**File**: `app/src/main/java/com/inspection/fragments/VoiceNoteBottomSheet.kt` (new)

| Property | Value |
|---|---|
| Superclass | `BottomSheetDialogFragment` |
| Package | `com.inspection.fragments` |
| Layout | `bottom_sheet_voice_input.xml` |
| Factory method | `fun show(fm: FragmentManager, onSave: (String) -> Unit)` |

**No data persisted by this class.** The `onSave` callback passes the recognized text
string back to `TodayVisitationFragment`, which calls `updateVisitationNotes()` as before.
