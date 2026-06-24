# Phase 0 Research: Specialists Performance Report PDF

All `NEEDS CLARIFICATION` items from the plan's Technical Context resolved here. Each decision is grounded in either the existing codebase, a measurement, or a stated trade-off — never speculation.

---

## R-001: Chart-rendering library

**Decision**: Use **matplotlib** for all charts (donuts, vertical bars, stacked bars, horizontal bars, sparklines) and for the US choropleth.

**Rationale**:
- The existing prod stack is Python 3.9 + Flask + mod_wsgi. matplotlib is the most widely-supported Python chart lib for in-memory PNG rendering.
- Render-to-PNG path: `fig.savefig(BytesIO(), format='png', dpi=120, bbox_inches='tight')` returns bytes that iText embeds via `Image.getInstance(bytes)` — already the pattern used by other backend PDF generators in this codebase.
- Sufficient for the choropleth via `matplotlib.patches.PathPatch` reading a US-states GeoJSON. No geopandas required.

**Alternatives considered**:
- **plotly + kaleido**: Native choropleth via `locationmode='USA-states'` — cleaner code, but the kaleido binary is ~150 MB and requires a Chromium headless install. Operationally heavier; rejected.
- **cairosvg + a static US-states SVG**: Color-substitute fills then rasterize. Lightweight (~5 MB), but means we'd need a second library (cairosvg) AND a separate code path for non-map charts. Inferior to matplotlib-only.
- **MPAndroidChart (constitution-approved)**: Android-only. Doesn't apply to Python backend.

---

## R-002: US states GeoJSON source

**Decision**: Check a single `us_states.geojson` (~150 KB) into `web-api-inspectionprod/assets/`. Use the public-domain US Census Bureau cartographic boundary file at the **20m resolution** (1:20,000,000), simplified to <200 KB.

**Rationale**:
- 20m resolution is enough for a full-page choropleth at 120 dpi. Higher-resolution shapefiles add disk weight with no visible improvement at our render size.
- Bundling the asset eliminates a runtime network dependency and a third-party API rate limit.
- The file is public domain — no licensing concern.

**Alternatives considered**:
- Fetch from a CDN at job start: introduces a network failure mode for every report. Rejected.
- 500k resolution (~2 MB): too detailed; bloats the deploy artifact. Rejected.

---

## R-003: Single-job concurrency gate

**Decision**: Module-level `threading.Lock` acquired non-blockingly. The Flask route attempts `lock.acquire(blocking=False)`. If `False`, return 409 immediately. If `True`, dispatch the worker thread, which releases the lock in a `finally`.

**Rationale**:
- FR-014 mandates "exactly one job in flight at a time". `Lock` is the minimal primitive for this. Built into stdlib — zero new deps.
- mod_wsgi reuses the Python interpreter across requests within a worker, so the module-level lock survives between requests. We accept the documented caveat that if Apache spawns multiple WSGI worker processes, each has its own lock and concurrency could rise to N. **Inspected**: `/etc/apache2/mods-enabled/wsgi.conf` on prod sets `WSGIDaemonProcess` with `processes=1`. One process → one lock → one job. Verified.

**Alternatives considered**:
- Database-row lock (`UPDATE tblSpecialistsPerfPDFLog SET ... WHERE status='running'`): more complex, no real benefit at our scale, and risks lock-row orphaning on crashes. Rejected.
- File-system lock (`fcntl.flock`): works across worker processes but is overkill given the verified `processes=1` config. Rejected.

---

## R-004: Background dispatch — threading vs Celery vs APScheduler

**Decision**: `threading.Thread(target=generate_and_email, args=(...)).start()`. Daemon=False so the worker can finish even if the request thread completes.

**Rationale**:
- One job at a time and ≤5-minute runtime mean no queueing needs. Celery + Redis would add infrastructure with zero feature value at this scale.
- mod_wsgi tolerates background threads when `WSGIDaemonProcess display-name=...` keeps the worker alive between requests. Other endpoints in this codebase already use this pattern (e.g., the existing `/refreshPrgRspSummary` cron-style refresh that runs minutes-long).
- Job output state (audit table) gives the operator the visibility that would otherwise come from a queue's job status API.

**Alternatives considered**:
- **Celery + Redis**: heavyweight for a 1-job, 1-process backend. Rejected.
- **APScheduler**: built for scheduled jobs, not one-shot dispatch. Rejected.
- **Synchronous in-request**: would block the trigger 60–300 s, violating SC-001's "≤3 s ack". Rejected.

---

## R-005: Email delivery path

**Decision**: Reuse the existing `sendPRGPDFTo(recipient, filename, type, full_path)` helper in `WebServices.py`. Pass `type='SpecialistsPerf'` for telemetry, `full_path` to the generated PDF.

**Rationale**:
- Helper already wraps the prod mail relay's auth + envelope handling.
- Existing endpoints (`/sendPRGCompletedPDF`, `/uploadPRGFile`) already use it; same retry/error path applies.
- Transient SMTP retry behavior (FR-017) is satisfied by whatever the relay already does — confirmed via inspection: `sendPRGPDFTo` retries 3 times with 5s backoff on SMTP transient codes.

**Alternatives considered**:
- Direct `smtplib` calls: would duplicate the relay-auth boilerplate. Rejected.
- SendGrid / external service: introduces a new dep and credentials story. Rejected.

---

## R-006: Audit table schema and lifecycle

**Decision**: `tblSpecialistsPerfPDFLog` in the PROD Inspection DB:

```sql
CREATE TABLE dbo.tblSpecialistsPerfPDFLog (
    recordid       INT IDENTITY(1,1) PRIMARY KEY,
    started_at     DATETIME       NOT NULL,
    finished_at    DATETIME       NULL,
    from_date      DATE           NOT NULL,
    to_date        DATE           NOT NULL,
    rows_processed INT            NULL,
    status         VARCHAR(20)    NOT NULL,    -- 'running' | 'ok' | 'failed'
    error          NVARCHAR(1000) NULL
);
CREATE INDEX IX_SpecialistsPerfPDFLog_status ON dbo.tblSpecialistsPerfPDFLog (status);
```

**Rationale**:
- One row per job (FR-013, FR-014).
- `status` index keeps the "is anything running?" query trivial.
- `rows_processed` is the visit-row count returned by the source query — a cheap reconciliation signal for operators.
- `error` capped at 1000 chars — enough for a stack-trace line or SMTP failure code without bloating the table.

**Lifecycle**:
1. Route accepts trigger → INSERT row with `status='running'`.
2. Worker thread runs → on success, UPDATE `status='ok', finished_at, rows_processed`.
3. On failure (any uncaught exception, including SMTP exhaustion), UPDATE `status='failed', finished_at, error`.
4. Rejected (lock not acquired) triggers DO NOT touch this table (FR-014).

---

## R-007: Window-validation order

**Decision**: Validate in this order before any DB call:
1. `from` and `to` are both present as query params.
2. Both parse as `YYYY-MM-DD` (strict ISO).
3. `from <= to`.
4. `(to - from).days >= 28`.

If any fail, return 400 with a single-sentence message identifying which check failed.

**Rationale**:
- Cheap to validate; never burn DB time on bad input.
- Step 4 is the new minimum-window check from Clarifications Q3.
- A single-sentence message is the existing error-style convention used elsewhere in `WebServices.py`.

**Alternatives considered**:
- Accept any window and warn in the PDF: confusing for operators, also wastes job slots. Rejected.

---

## R-008: Club → State mapping snapshot timing

**Decision**: At job start (after acquiring the lock), the worker runs ONE CSI query: `SELECT DISTINCT ClubCode, aaastate FROM csi.dbo.AAAFacilities WHERE aaastate IS NOT NULL`. Result is loaded into a Python dict and used for the entire job.

**Rationale**:
- FR-005 forbids a cross-DB join. A standalone CSI query is the cheapest alternative.
- The mapping is small (~250 rows) and stable within a job's lifetime. No reason to re-query.
- Unmapped clubs (clubs in `tblPRGPDFMaster` but not in the dict) get tallied in a counter and surfaced as the FR-009 footnote on the map page.

**Alternatives considered**:
- Cache the mapping across jobs in module state: invalidation gets ugly. Per-job is simpler. Rejected.
- One CSI query per club: 250 round-trips. Rejected.

---

## R-009: PDF page size and brand

**Decision**: A4 landscape, 595×842 pt landscape orientation (842 wide × 595 tall). Top masthead 60 pt tall, filled with the existing `cert_header_bg` gradient (`#073763` → `#1565C0`, horizontal).

**Rationale**:
- FR-015 requires the brand masthead. The same gradient already paints the in-app toolbar so the report feels like it came from the same product.
- A4 landscape gives the dashboard 6 charts room to breathe at 2×3 without crowding. US Letter landscape (792×612 pt) is similar enough that operators won't be confused on print.

**Alternatives considered**:
- US Letter portrait: dashboard charts get cramped. Rejected.
- A4 portrait: choropleth becomes awkward (US is wider than tall). Rejected.

---

## R-010: Constitution alignment — Android URL constant (future work)

**Decision**: NOT adding the endpoint to `Utils/Constants.kt`. There is no v1 Android caller per spec scope.

**Future-work note**: If v2 adds an Android trigger button (e.g., in the director's admin tools area), the URL MUST be added to `Constants.kt` per Principle III (Flavor-Aware, No Hardcoded Endpoints). Until then, the URL is operator-only and the prod backend is the single source of truth.

**Rationale**: Premature `Constants.kt` entries for unreferenced endpoints are dead code per the project's terseness preferences.

---

## R-011: Empty-window rendering

**Decision**: When the source query returns zero rows, the worker still generates the PDF: cover shows "0 visits" / "0 active specialists", dashboard charts show a centered "No visits in period" label, choropleth renders blank states, leaderboard table shows "No specialists with visits in this period", per-specialist section is omitted entirely.

**Rationale**:
- FR-016 mandates rendering. Operators get a meaningful artifact rather than a misleading "blank report".
- Per-specialist section is the only piece omitted: rendering zero cards adds no value and risks an "is the PDF cut off?" support ticket.

---

## R-012: Open-access endpoint hardening considerations

**Decision**: Endpoint is open (FR-018) but the worker rejects pathologically wide windows synchronously to avoid resource abuse. Hard cap: `(to - from).days <= 1095` (3 years). Anything wider returns 400 with "Maximum window is 3 years".

**Rationale**:
- Even though FR-014's single-job gate prevents amplification, one runaway trigger with a 50-year window could allocate enormous matplotlib buffers.
- 3 years comfortably exceeds any realistic business report period (annual, quarterly, multi-year board review).
- Synchronous rejection costs nothing — same code path as the under-28-day check.

**Alternatives considered**:
- No cap, rely on `processes=1` to absorb the load: a stuck job could still spike memory for minutes. Rejected.
- 1-year cap: surprised by user requests for multi-year roll-ups. Too restrictive. Rejected.

---

## R-013: Chart color palette

**Decision**: Charts use a fixed 4-color blue palette interpolated between the masthead gradient endpoints (`#073763` → `#1565C0`), plus a neutral grey for "waived" segments:

| Slot | Hex | Used for |
|---|---|---|
| Primary dark | `#073763` | Donut slice 1 (Annual / In Person); horizontal bar fill for top-N charts; deepest choropleth shade. |
| Primary mid-dark | `#1A4E8A` | Donut slice 2 (Quarterly / Phone); stacked-bar "Performed" segment. |
| Primary mid-light | `#3870B6` | Donut slice 3 (AdHoc / Email); vertical-bar "Visits per Month" fill. |
| Primary light | `#5A92D4` | Donut slice 4 (Deficiency / Web Conference); sparkline fill; lightest choropleth shade. |
| Waived grey | `#9CA8B5` | Stacked-bar "Waived" segment only. |

**matplotlib equivalent**: `mpl.colors.LinearSegmentedColormap.from_list('inspection_blues', ['#073763', '#5A92D4'])` for the choropleth; the 4 hex slot values used directly for donut and bar charts.

**Rationale**:
- Anchors all charts visually to the masthead gradient so the report reads as a single document.
- 4 discrete slots maps cleanly to the 4-slice donuts (method, type) without re-ordering.
- A neutral grey for "waived" keeps that segment visually separable from the blue performed segments in the stacked bar without introducing red (which would imply alert / failure).
- Deterministic — no implicit matplotlib default palette that could shift between versions.

**Alternatives considered**:
- matplotlib `Blues` colormap default: shifts between matplotlib versions. Rejected for determinism.
- Multi-hue (red / green / blue): suggests good/bad value judgments, which the spec explicitly rejected ("no targets, no green/amber/red thresholds"). Rejected.
- Single blue with alpha variations: looks washed out at PDF rendering size. Rejected.

---

## R-014: matplotlib version pin

**Decision**: Pin to `matplotlib==3.9.*`. Install command becomes `pip install 'matplotlib==3.9.*'`.

**Rationale**:
- Constitution Principle V means no tests catch a silent API change.
- Pinning the minor line (`3.9.*`) keeps security patches reachable while preventing 4.x breaking changes from leaking in via a future `pip install --upgrade`.
- 3.9 is current at writing (2026-06-24) and has been stable for >12 months; its choropleth-via-PathPatch path and `from_list` colormap API are unchanged in this line.

**Alternatives considered**:
- No pin: silently break on next deploy of an unrelated dep. Rejected.
- Exact pin `matplotlib==3.9.0`: forbids security patches. Rejected.
- `>=3.9`: lets future 4.x in. Rejected.
