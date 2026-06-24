# Phase 1 Data Model: Specialists Performance Report PDF

## 1. Source entities (read-only)

### 1.1 `tblPRGPDFMaster` (PROD Inspection DB)

The single source of all visit data. Only these columns are read:

| Column | SQL type | Used for |
|---|---|---|
| `performeddate` | `varchar` / `datetime` (string-formatted as `MM/DD/YYYY`) | Window filter; monthly bar chart bucketing; sparkline bucketing. |
| `specialist` | `varchar` | Grouping key for leaderboard and mini-cards. |
| `visitationmethod` | `varchar` ('In Person' / 'Phone' / 'Email' / 'Web Conference') | Method donut; In-Person % KPI. |
| `visitationtype` | `varchar` ('Annual' / 'Quarterly' / 'AdHoc' / 'Deficiency') | Type donut; leaderboard type columns; per-specialist type donut. |
| `visitationreason` | `varchar` | Top-10 Reasons horizontal bar. |
| `waivevisitation` | `bit` (0 / 1) | Performed-vs-Waived stacked bar; Waived % KPI; leaderboard Waived % column. |
| `facno` | `varchar` | Facilities count; leaderboard Facilities column. |
| `clubcode` | `varchar` (e.g. `'036'`) | Top-10 Clubs bar; leaderboard Clubs count; choropleth via state mapping. |

**Window filter**: `WHERE TRY_CONVERT(DATE, performeddate, 101) BETWEEN @from AND @to`. The `101` style is `MM/DD/YYYY`, matching the format observed in row samples.

### 1.2 `csi.dbo.AAAFacilities` (PROD CSI DB)

Used only to build the club → state dictionary, once per job:

```sql
SELECT DISTINCT ClubCode, aaastate
FROM csi.dbo.AAAFacilities
WHERE aaastate IS NOT NULL;
```

Result loaded into `dict[str, str]` keyed by `clubcode` (left-zero-padded to match the format in `tblPRGPDFMaster`).

## 2. Writable entity

### 2.1 `tblSpecialistsPerfPDFLog` (PROD Inspection DB, NEW)

```sql
CREATE TABLE dbo.tblSpecialistsPerfPDFLog (
    recordid       INT IDENTITY(1,1) PRIMARY KEY,
    started_at     DATETIME       NOT NULL,
    finished_at    DATETIME       NULL,
    from_date      DATE           NOT NULL,
    to_date        DATE           NOT NULL,
    rows_processed INT            NULL,
    status         VARCHAR(20)    NOT NULL,
    error          NVARCHAR(1000) NULL
);
CREATE INDEX IX_SpecialistsPerfPDFLog_status
    ON dbo.tblSpecialistsPerfPDFLog (status);
```

**State machine**:

```text
              ┌─────────────┐
trigger ────► │  running    │ ──── on success ────► ok      (terminal)
              │             │ ──── on exception ──► failed  (terminal)
              └─────────────┘
```

**Invariants**:
- Exactly one row inserted per accepted trigger.
- `started_at` set at INSERT; never updated.
- `finished_at`, `rows_processed`, `error` mutated only on the terminal transition.
- A rejected trigger (lock already held) DOES NOT insert a row.

## 3. In-memory entities

### 3.1 `Visit`

Direct projection of a row read from `tblPRGPDFMaster`. Held only for the duration of a single aggregation pass.

| Field | Type | Source column |
|---|---|---|
| `performed_date` | `datetime.date` | `performeddate` parsed with `MM/DD/YYYY` |
| `specialist` | `str` | `specialist` |
| `method` | `str` | `visitationmethod` |
| `type` | `str` | `visitationtype` |
| `reason` | `str` | `visitationreason` |
| `is_waived` | `bool` | `waivevisitation == 1` |
| `facno` | `str` | `facno` |
| `clubcode` | `str` (zero-padded) | `clubcode` |

### 3.2 `SpecialistAggregate`

One per distinct specialist with ≥1 visit in window. Computed by the aggregator.

| Field | Type | Derivation |
|---|---|---|
| `name` | `str` | `specialist` |
| `total_visits` | `int` | `COUNT(*)` |
| `in_person_pct` | `float` (0–100, 1dp) | `100 * COUNT(method == 'In Person') / total_visits` |
| `waived_pct` | `float` (0–100, 1dp) | `100 * COUNT(is_waived == True) / total_visits` |
| `annual` | `int` | `COUNT(type == 'Annual')` |
| `quarterly` | `int` | `COUNT(type == 'Quarterly')` |
| `adhoc` | `int` | `COUNT(type == 'AdHoc')` |
| `deficiency` | `int` | `COUNT(type == 'Deficiency')` |
| `facility_count` | `int` | `COUNT(DISTINCT facno)` |
| `club_count` | `int` | `COUNT(DISTINCT clubcode)` |
| `method_mix` | `dict[str, int]` | `{method: count}` for mini-card method donut |
| `type_mix` | `dict[str, int]` | `{type: count}` for mini-card type donut |
| `visits_by_month` | `dict[str, int]` | `{'YYYY-MM': count}` for mini-card monthly sparkline |

### 3.3 `OverallAggregate`

Top-level numbers driving the cover + dashboard.

| Field | Type | Derivation |
|---|---|---|
| `total_visits` | `int` | `len(visits)` |
| `active_specialists` | `int` | `len(distinct specialist)` |
| `facilities_covered` | `int` | `len(distinct facno)` |
| `in_person_pct` | `float` | `100 * COUNT(method == 'In Person') / total_visits` |
| `waived_pct` | `float` | `100 * COUNT(is_waived) / total_visits` |
| `method_counts` | `dict[str, int]` | for dashboard Method donut (4 slices) |
| `type_counts` | `dict[str, int]` | for dashboard Type donut (4 slices) |
| `monthly_counts` | `dict[str, int]` | `{'YYYY-MM': count}` — Visits per Month bar |
| `monthly_waived_counts` | `dict[str, int]` | per-month waived counts — Performed-vs-Waived stack |
| `top_reasons` | `list[(reason, count)]` | sorted desc, head=10 — Top-10 Reasons bar |
| `top_clubs` | `list[(clubcode, count)]` | sorted desc, head=10 — Top-10 Clubs bar |
| `state_counts` | `dict[str, int]` | `{state_code: count}` after mapping clubcode → state |
| `unmapped_visits` | `int` | count of visits whose clubcode had no state — choropleth footnote |

### 3.4 `ClubStateMapping`

Loaded once per job:

```python
mapping: dict[str, str]   # '036' -> 'CA'
```

## 4. Validation rules (input)

These run synchronously in the route handler before the worker is dispatched:

| Rule | Source | Failure response |
|---|---|---|
| `from` query param present | FR-002 | 400 "from is required" |
| `to` query param present | FR-002 | 400 "to is required" |
| `from` parses as `YYYY-MM-DD` | FR-002 | 400 "from must be YYYY-MM-DD" |
| `to` parses as `YYYY-MM-DD` | FR-002 | 400 "to must be YYYY-MM-DD" |
| `from <= to` | FR-002 | 400 "from must be on or before to" |
| `(to - from).days >= 28` | FR-002 (Clarif. Q3) | 400 "Window must be at least 28 days" |
| `(to - from).days <= 1095` | R-012 | 400 "Maximum window is 3 years" |

## 5. Reconciliation guarantees

- Sum of slices in any donut equals `total_visits` (or specialist's `total_visits`).
- Sum of `state_counts.values()` + `unmapped_visits` equals overall `total_visits`.
- Sum of monthly_counts equals `total_visits`.
- Sum of `monthly_waived_counts` equals `COUNT(is_waived)`.

These invariants make spot-check reconciliation (SC-003) deterministic.
