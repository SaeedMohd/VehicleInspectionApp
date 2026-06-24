---
description: "Task list for Specialists Performance Report PDF (feature 009)"
---

# Tasks: Specialists Performance Report PDF

**Input**: Design documents from `/specs/009-specialists-perf-pdf/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/http-api.md, quickstart.md

**Tests**: NONE. Constitution Principle V — "No Automated Test Infrastructure" — explicitly forbids unit/integration/contract test tasks for this codebase. Verification is via the manual smoke tasks in the Polish phase.

**Organization**: Tasks are grouped by user story so each story can be implemented and demoed independently.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Parallelizable (different files, no dependencies on incomplete tasks)
- **[Story]**: Maps task to a user story (US1, US2, US3); only used in user-story phases
- Every task carries an exact file path

## Path conventions (from plan.md → Project Structure)

- **Prod backend tree**: `web-api-inspectionprod/`
  - Route: `web-api-inspectionprod/WebServices.py`
  - Module: `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`
  - Asset: `web-api-inspectionprod/assets/us_states.geojson`
  - DDL: `web-api-inspectionprod/sql/2026-06-24_tblSpecialistsPerfPDFLog.sql`
- **UAT backend tree** (mirror only): `web-api-inspectionuat/app/`
- **No Android changes in v1.**

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Repository scaffolding for the feature.

- [ ] T001 Create the audit-table DDL file at `web-api-inspectionprod/sql/2026-06-24_tblSpecialistsPerfPDFLog.sql` with the exact schema from data-model.md §2.1 (table + status index).
- [ ] T002 [P] Build the US states GeoJSON asset and save at `web-api-inspectionprod/assets/us_states.geojson` (see research.md R-002). One-time procedure:
  ```bash
  # 1. Download the Census Bureau 20m cartographic boundary shapefile
  curl -O https://www2.census.gov/geo/tiger/GENZ2023/shp/cb_2023_us_state_20m.zip
  unzip cb_2023_us_state_20m.zip
  # 2. Convert to GeoJSON with state postal codes as feature ids (requires gdal / ogr2ogr)
  ogr2ogr -f GeoJSON us_states_full.geojson cb_2023_us_state_20m.shp \
    -sql "SELECT STUSPS as id, NAME FROM cb_2023_us_state_20m"
  # 3. Simplify to <200 KB (requires mapshaper: npm install -g mapshaper)
  mapshaper us_states_full.geojson -simplify 5% -o web-api-inspectionprod/assets/us_states.geojson
  ```
  Verify with `python3 -c "import json; d=json.load(open('web-api-inspectionprod/assets/us_states.geojson')); print(len(d['features']), 'features,', sum(len(json.dumps(f)) for f in d['features']), 'bytes')"`.
- [ ] T003 [P] Create module skeleton at `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py` with imports (`threading`, `datetime`, `json`, `io.BytesIO`, `matplotlib`, iText classes via the existing helper, `DbConnection`), a module-level `Lock`, the hardcoded recipient constant `RECIPIENT_EMAIL = 'saeed@pacificresearchgroup.com'`, and empty placeholder functions: `validate_window`, `acquire_lock`, `release_lock`, `fetch_visits`, `fetch_club_state_mapping`, `aggregate`, `render_pdf`, `email_pdf`, `generate_and_email`.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared building blocks every user story will use.

**⚠️ CRITICAL**: No user story phase can start until this phase is complete.

- [ ] T004 Apply DDL to PROD Inspection DB: scp `sql/2026-06-24_tblSpecialistsPerfPDFLog.sql` to `192.168.75.105`, then run via `DbConnection.updateDB(open('/tmp/...').read())` from the prod venv (see quickstart.md deploy section). Verify via `SELECT TOP 1 * FROM dbo.tblSpecialistsPerfPDFLog`.
- [ ] T005 [P] Install matplotlib in the prod venv, pinned to the 3.9.x line per research.md R-014: `ssh smostafa@192.168.75.105 "echo '<pw>' | sudo -S /opt/venv3/bin/pip install 'matplotlib==3.9.*'"`. Confirm with `ssh ... "/opt/venv3/bin/python3 -c 'import matplotlib; print(matplotlib.__version__)'"` — expect a `3.9.x` version string.
- [ ] T006 In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `acquire_lock()` returning `bool` via `_lock.acquire(blocking=False)`, and `release_lock()` calling `_lock.release()` only if currently held. Module-level `_lock = threading.Lock()` (research.md R-003).
- [ ] T007 In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `validate_window(from_str, to_str)` per data-model.md §4: returns `(from_date, to_date)` on success; raises `ValidationError` with a single-sentence message for each failure case (missing, unparseable, `from > to`, <28 days, >1095 days).
- [ ] T008 [P] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `fetch_club_state_mapping()` running `SELECT DISTINCT ClubCode, aaastate FROM csi.dbo.AAAFacilities WHERE aaastate IS NOT NULL` via `DbConnection.queryCsiDB`, returning `dict[str, str]` keyed by zero-padded clubcode.
- [ ] T009 [P] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `fetch_visits(from_date, to_date)` running the windowed `SELECT` against `tblPRGPDFMaster` (data-model.md §1.1) via `DbConnection.queryDb`, returning `list[Visit]` where `Visit` is a `@dataclass` with the 8 fields from §3.1.
- [ ] T010 In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `aggregate(visits, mapping)` producing an `OverallAggregate` and a `dict[str, SpecialistAggregate]` per data-model.md §3.2 and §3.3. Include the `unmapped_visits` count derived from `mapping.get(clubcode)`.
- [ ] T011 In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `_pdf_chrome(doc)`: page-setup helper for A4 landscape, draws the `cert_header_bg` gradient (#073763 → #1565C0, horizontal) as a 60pt masthead, draws a page-number footer in the format `Page N of M` (8pt Helvetica, right-aligned, 20pt above bottom margin). Returns a content area `Rectangle` for the calling renderer (research.md R-009).

**Checkpoint**: Foundation ready. Each user story can now proceed.

---

## Phase 3: User Story 1 — On-demand Performance Report for a Date Range (Priority: P1) 🎯 MVP

**Goal**: A `curl -X POST` with valid `from` / `to` returns `202` immediately, and 5 minutes later the recipient receives a PDF with a cover, summary dashboard, and US choropleth map. This is the entire MVP — without it, there is no product.

**Independent Test**: Fire `curl -X POST 'https://inspection.valueaddedonline.com/generateSpecialistsPerfPDF?from=2026-04-01&to=2026-06-30'`. Within 3 s receive `202`; within 5 min the recipient inbox contains a PDF whose cover totals reconcile against `SELECT COUNT(*), COUNT(DISTINCT specialist) FROM tblPRGPDFMaster WHERE performeddate BETWEEN ...`.

### Charts (chart-rendering helpers — parallelizable)

- [ ] T012 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_method_donut(method_counts) -> bytes` using matplotlib (4 slices, percentage labels). Slot colors from research.md R-013: `#073763`, `#1A4E8A`, `#3870B6`, `#5A92D4` (mapped to In Person, Phone, Email, Web Conference in that order).
- [ ] T013 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_type_donut(type_counts) -> bytes` (4 slices, R-013 palette mapped to Annual, Quarterly, AdHoc, Deficiency).
- [ ] T014 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_monthly_bar(monthly_counts) -> bytes` (vertical bars, x-axis = `YYYY-MM`, y-axis = count, fill `#3870B6` from R-013).
- [ ] T015 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_performed_vs_waived(monthly_counts, monthly_waived_counts) -> bytes` (stacked vertical bar; "Performed" segment `#1A4E8A`, "Waived" segment `#9CA8B5` per R-013).
- [ ] T016 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_top_reasons(top_reasons) -> bytes` (horizontal bar, top 10, sorted desc, fill `#073763` from R-013).
- [ ] T017 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_top_clubs(top_clubs) -> bytes` (horizontal bar, top 10, with clubcode + count overlay, fill `#073763` from R-013).
- [ ] T018 [P] [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_us_choropleth(state_counts, total_visits, unmapped_visits) -> bytes` — load `assets/us_states.geojson`, color each state by `state_counts.get(code, 0) / total_visits` using `LinearSegmentedColormap.from_list('inspection_blues', ['#5A92D4', '#073763'])` from R-013 (light → deep, so zero-visit states are lightest and the highest-share state is deepest blue), label the top 3 states with their percentage, render the unmapped count as a small footnote inside the figure.

### Page renderers (depend on chart helpers + aggregator)

- [ ] T019 [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_cover(doc, overall, from_date, to_date, generated_at)` showing title, date range, generated timestamp, `total_visits`, `active_specialists`. Depends on T010, T011.
- [ ] T020 [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_dashboard(doc, overall)` — KPI strip of 5 tiles (Total Visits, Active Specialists, Facilities Covered, In-Person %, Waived %), then the 2×3 chart grid embedding the PNG outputs of T012-T017 via `Image.getInstance(bytes)`. Depends on T010, T011, T012-T017.
- [ ] T021 [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_map_page(doc, overall)` — full-page embed of T018's PNG. Depends on T018.
- [ ] T022 [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, extend `render_cover` / `render_dashboard` / `render_map_page` to replace charts and KPI numbers with a centered "No visits in period" label when `overall.total_visits == 0` (FR-016, research.md R-011).

### Delivery + orchestration

- [ ] T023 [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `email_pdf(full_path, filename)` calling `sendPRGPDFTo(RECIPIENT_EMAIL, filename, 'SpecialistsPerf', full_path)`. Trust the existing helper's retry behavior per research.md R-005; let exceptions propagate.
- [ ] T024 [US1] In `web-api-inspectionprod/WebServices.py`, add the Flask route `@app.route('/generateSpecialistsPerfPDF', methods=['POST'])`: parse `from` / `to` query params, call `validate_window`, attempt `acquire_lock()`, on success INSERT a `running` row into `tblSpecialistsPerfPDFLog`, kick off `threading.Thread(target=generate_and_email, args=(from_date, to_date, recordid)).start()`, return 202 JSON per contracts/http-api.md. Imports the module from T003.
- [ ] T025 [US1] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `generate_and_email(from_date, to_date, recordid)` — fetch mapping (T008), fetch visits (T009), aggregate (T010), render cover/dashboard/map (T019/T020/T021), write PDF to `/pdfs/SpecialistsPerf_<from>_<to>_<recordid>.pdf`, email (T023), then UPDATE the audit row to `status='ok'` with `finished_at=GETUTCDATE()`, `rows_processed=len(visits)`. The lock release goes in this function's `finally`.

**Checkpoint**: US1 complete — trigger → 202 → email arrives → dashboard + map are accurate. US2 and US3 not yet implemented.

---

## Phase 4: User Story 2 — Per-Specialist Leaderboard and Snapshots (Priority: P2)

**Goal**: Add the leaderboard table + per-specialist mini-cards so the recipient can see individual performance, not just aggregate.

**Independent Test**: Generate a report. For one known specialist, verify that their leaderboard row's visit count, in-person %, and waived % match a hand-aggregation. Verify the same specialist's mini-card shows the same numbers plus a method donut and type donut.

- [ ] T026 [P] [US2] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_leaderboard(doc, per_specialist)` — iText `PdfPTable` with 10 columns per data-model.md §3.2, neutral-blue header, paginates at 30 rows/page across multiple PDF pages, sorted by `total_visits` descending. Depends on T010 (per-specialist aggregator output).
- [ ] T027 [P] [US2] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_mini_donut(counts, palette) -> bytes` — smaller variant of T012/T013 sized for a 1/3-page mini-card.
- [ ] T028 [P] [US2] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_monthly_sparkline(visits_by_month) -> bytes` — narrow bar chart, no y-axis labels, axis-aligned to the report's window (data-model.md §3.2).
- [ ] T029 [US2] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_specialist_card(doc, spec)` — 1/3-page block showing name, total visits, method donut (T027), type donut (T027), In-Person %, Waived %, facility count, monthly sparkline (T028). Depends on T027, T028.
- [ ] T030 [US2] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, implement `render_specialists_section(doc, per_specialist)` — paginates 3 cards per page through the per-specialist dict. Depends on T029.
- [ ] T031 [US2] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, hook `render_leaderboard` and `render_specialists_section` into `generate_and_email` after the map page (T025), before the email send (T023). Depends on T026, T030.

**Checkpoint**: US1 + US2 — emailed PDF now contains cover, dashboard, map, leaderboard, and per-specialist cards.

---

## Phase 5: User Story 3 — Reliable Job Tracking and Failure Visibility (Priority: P3)

**Goal**: Operators can answer "did this run? did it fail? what failed?" from the audit table alone, without log digging. Concurrency is gated to one job at a time.

**Independent Test**: (1) Fire two `curl` triggers back-to-back; the second returns 409. (2) Induce a controlled failure (e.g., point the visits query at a non-existent column); confirm the audit row transitions to `status='failed'` with the error message visible.

- [ ] T032 [P] [US3] In `web-api-inspectionprod/WebServices.py`, in the route handler (T024), wrap the lock-acquire result: when `acquire_lock()` returns `False`, return 409 JSON `{"status":"busy","message":"A report is already being generated. Try again when the running job completes."}` and DO NOT INSERT into the audit table (FR-014).
- [ ] T033 [P] [US3] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, wrap the body of `generate_and_email` in `try / except Exception as e / finally`: in `except`, UPDATE the audit row to `status='failed', finished_at=GETUTCDATE(), error=str(e)[:1000]`; in `finally`, always call `release_lock()`. Depends on T025.
- [ ] T034 [P] [US3] In `web-api-inspectionprod/WebServices.py` (route from T024), enforce the validate-then-INSERT order with an inline guard. Structure the route body so `validate_window()` runs first and any `ValidationError` raised by it short-circuits to `400` BEFORE any audit-table INSERT or lock acquisition. Add a one-line comment immediately above the INSERT: `# IMPORTANT: never INSERT before validation succeeds — bad requests must not leave audit rows behind`.
- [ ] T035 [P] [US3] In `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, ensure transient SMTP failures from `sendPRGPDFTo` surface as `Exception` and flow into the T033 failure path. The audit row's `error` should contain the SMTP code or last exception message (FR-017).

**Checkpoint**: US1 + US2 + US3 — all three priority slices in place.

---

## Phase 6: Deploy & Mirror

**Purpose**: Move the code from the repo onto the prod box (the only place v1 actually runs) and mirror to UAT for parity per spec scope.

- [ ] T036 [P] Deploy to prod: `scp` `web-api-inspectionprod/SpecialistsPerfPDFGenerator.py`, `web-api-inspectionprod/WebServices.py`, and `web-api-inspectionprod/assets/us_states.geojson` to their respective paths on `192.168.75.105` (see quickstart.md), then `sudo systemctl reload apache2`. Verify with `curl -sk https://inspection.valueaddedonline.com/generateSpecialistsPerfPDF?from=2026-04-01&to=2026-06-30 -X POST -w '\n%{http_code}\n%{time_total}s\n'` — expect HTTP 202 in <3 s.
- [ ] T037 [P] Mirror to UAT: `scp` `web-api-inspectionuat/app/SpecialistsPerfPDFGenerator.py` (port from prod with `query_db` snake_case per memory `reference_servers.md`) and updated `web-api-inspectionuat/app/WebServices.py` to `192.168.74.116`, then `docker restart web-api-inspectionuat`.
- [ ] T038 Apply DDL to UAT local Inspection DB: `docker exec web-api-inspectionuat /venv/bin/python3 -c "import sys; sys.path.insert(0,'/app'); from static.DbConnection import DbConnection; print(DbConnection.updateDB(open('/app/sql/2026-06-24_tblSpecialistsPerfPDFLog.sql').read()))"`.

---

## Phase 7: Polish & Verification (Manual smoke per Constitution V — no automated tests)

**Purpose**: Confirm the deployed feature behaves per spec across the named acceptance scenarios.

- [ ] T039 [P] Smoke US1 happy path: `curl -X POST 'https://inspection.valueaddedonline.com/generateSpecialistsPerfPDF?from=2026-04-01&to=2026-06-30'`, assert HTTP 202 in <3 s, audit row appears with `status='running'`, email arrives at `saeed@pacificresearchgroup.com` within 5 min, audit row transitions to `status='ok'` (SC-001, SC-002, Story 1 scenarios 1-4).
- [ ] T040 [P] Smoke validation errors: fire `curl` with (a) no `from`, (b) `from > to`, (c) 14-day window (under the 28-day floor), (d) 1100-day window (just past the 3-year / 1095-day cap). All return 400 with one-sentence message; no audit row created (FR-002, edge case "Invalid date arguments").
- [ ] T041 [P] Smoke concurrency: while a 90-day job is running, fire a second `curl`; expect 409 `{"status":"busy",...}` and no new audit row (FR-014, SC-005, US3 scenario 4).
- [ ] T042 [P] Smoke empty window: fire a 28-day window that has zero visits in the source data (use `from` / `to` in the future). Confirm PDF arrives with "No visits in period" placeholders on cover, dashboard, map; per-specialist section omitted (FR-016, R-011, edge case "Empty window").
- [ ] T043 [P] Smoke failure path: temporarily induce a controlled exception (e.g., DROP and re-CREATE the audit table with a typo'd column, fire trigger, immediately revert). Confirm the audit row transitions to `status='failed'` with `error` populated (SC-008, US3 scenario 3).
- [ ] T044 [P] Reconciliation check: pick one specialist from a known window, hand-aggregate `COUNT(*)`, `COUNT(method='In Person')`, `COUNT(waivevisitation=1)` directly via `DbConnection.queryDb`. Confirm the leaderboard row and the mini-card numbers match within ±1 percentage point (SC-003, US2 scenarios 1-3).
- [ ] T045 [P] Smoke unicode / unusual specialist names: identify a window known to contain at least one `specialist` value with an apostrophe, accented character, or 30+ character name (e.g. `SELECT TOP 5 specialist FROM tblPRGPDFMaster WHERE specialist LIKE '%''%' OR LEN(specialist) > 25`). Fire the trigger for a window covering that row. Confirm the leaderboard and the mini-card render the name without truncation, column overflow, or matplotlib font-glyph errors (spec edge case "Specialist name with unusual characters").
- [ ] T046 [P] Verify no-auth posture: confirm the route handler in `web-api-inspectionprod/WebServices.py` contains NO `request.headers.get('Authorization')`, NO token/secret check, and NO IP allow-list — and confirm an unauthenticated `curl` from outside the prod host (but on the same internal network) still receives `202`. This is a deliberate v1 stance per FR-018; the check exists so a future PR adding auth without lifting the FR-018 caveat trips on this task.
- [ ] T047 Update memory: write a new memory note at `~/.claude/projects/-Users-saeedmostafa-Work-Projects-Anthony-VehicleInspectionApp/memory/project_specialists_perf_pdf.md` summarising endpoint URL, audit table name, recipient email, the matplotlib version pin (`3.9.*`), and the GeoJSON asset path. Add it to MEMORY.md.

---

## Dependencies & Execution Order

### Phase dependencies

- Phase 1 (Setup) → no deps; can start immediately.
- Phase 2 (Foundational) → depends on Phase 1.
- Phase 3 (US1) → depends on Phase 2 complete.
- Phase 4 (US2) → depends on Phase 2 complete; can run in parallel with Phase 3 if staffed, but shares `SpecialistsPerfPDFGenerator.py` so most tasks are sequential within the same file.
- Phase 5 (US3) → depends on Phase 3's T024/T025 (it modifies them).
- Phase 6 (Deploy) → depends on US1 minimum (US2 and US3 can ship later).
- Phase 7 (Verification) → depends on Phase 6.

### User-story dependencies (per spec.md)

- US1 (P1) is the MVP. Phases 1 → 2 → 3 → 6 → some of 7 = shippable MVP.
- US2 (P2) can ship after US1; the leaderboard / mini-card tasks all touch the same module so cannot truly parallel-execute with US1 work.
- US3 (P3) modifies existing US1 code (route handler + worker); intentionally last in code order to layer failure/concurrency on a working happy path.

### Critical-path within US1

T001/T002/T003 → T004/T005 → T006/T007 → T008/T009 → T010 → T011 → (T012 || T013 || T014 || T015 || T016 || T017 || T018) → T019 → T020 → T021 → T022 → T023 → T024 → T025

**Implementation order note**: T024 (route) can be written first against the empty `generate_and_email` placeholder from T003 — it will compile and return 202, but the spawned thread will no-op. A full T039 smoke test (PDF arriving in email) requires T025 to be done. If a developer wants a smoke-able MVP after the first commit, implement T025 *before* T024.

### Parallel opportunities

- **Phase 1**: T002 + T003 can run together; T001 has no overlap.
- **Phase 2**: T005 (matplotlib install) || T008 (CSI mapping query) || T009 (visits query) all parallel.
- **US1 charts (T012-T018)**: 7 chart helpers, all parallel — they're independent functions.
- **US2 (T026, T027, T028)**: 3 renderer helpers, all parallel.
- **US3 (T032-T035)**: 4 wrapping/guard tasks, all parallel.
- **Phase 7 smoke checks**: all parallel.

---

## Parallel example: US1 chart helpers

```bash
# Launch all 7 chart renderers together (one file each operates on different functions
# in the same module — only safe if your editing tooling can serialize file writes):
Task: "Implement render_method_donut in SpecialistsPerfPDFGenerator.py (T012)"
Task: "Implement render_type_donut in SpecialistsPerfPDFGenerator.py (T013)"
Task: "Implement render_monthly_bar in SpecialistsPerfPDFGenerator.py (T014)"
Task: "Implement render_performed_vs_waived in SpecialistsPerfPDFGenerator.py (T015)"
Task: "Implement render_top_reasons in SpecialistsPerfPDFGenerator.py (T016)"
Task: "Implement render_top_clubs in SpecialistsPerfPDFGenerator.py (T017)"
Task: "Implement render_us_choropleth in SpecialistsPerfPDFGenerator.py (T018)"
```

---

## Implementation strategy

### MVP first (US1 only)

1. Phase 1 + Phase 2 → foundation ready.
2. Phase 3 (US1) → cover + dashboard + map page only.
3. Phase 6 → deploy to prod + UAT mirror.
4. T039 smoke → confirm MVP.
5. **STOP and validate** with stakeholder. Iterate copy / chart styling if needed.

### Incremental delivery

1. MVP (US1) ships first. Recipients see the aggregate report immediately.
2. US2 added: leaderboard + cards land in subsequent PDFs.
3. US3 added: failure visibility + concurrency gate. (Note: US3 is technically required for safe ops, so consider bundling US3 with the MVP rather than after US2 if production safety matters more than feature completeness.)

### Sequential execution (single developer — recommended for this codebase)

All US1, US2, US3 changes live in `SpecialistsPerfPDFGenerator.py` + `WebServices.py`. With one developer, parallelism within a file is limited — execute T001 → T047 sequentially within each phase, exploiting [P] only across phases where multiple distinct files are touched (deploy steps, GeoJSON download, matplotlib install).

---

## Notes

- No tests requested or generated. Constitution V applies.
- Every task lists the exact target file and exact symbol name where applicable.
- The route handler (T024) and worker (T025) are both touched again by US3 (T032, T033) — anticipate two passes.
- The audit table DDL must be applied to UAT (T038) only if you want UAT to host the endpoint behind the same contract. If UAT is "code parity only, no triggers", T038 is still required so the module's imports don't crash on first request.
- Memory update (T047) ensures future sessions know about the endpoint, the audit table name, the matplotlib pin, and the GeoJSON asset path without rediscovering them.
