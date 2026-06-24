# Implementation Plan: Specialists Performance Report PDF

**Branch**: `009-specialists-perf-pdf` | **Date**: 2026-06-24 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/009-specialists-perf-pdf/spec.md`

## Summary

An on-demand PDF report endpoint on the PROD Inspection backend. The endpoint accepts a `from` / `to` window, validates it synchronously, and kicks off a single background job that aggregates visit data from `tblPRGPDFMaster`, resolves club-to-state mapping from `csi.dbo.AAAFacilities` once per job, renders a cover + dashboard + US choropleth + leaderboard + per-specialist mini-cards into a single PDF, and emails the finished PDF to `saeed@pacificresearchgroup.com`. One job at a time — concurrent triggers are rejected synchronously. No auth gate — matches the existing posture of every endpoint on this backend.

## Technical Context

**Language/Version**: Python 3.9 (Apache + mod_wsgi/4.7.1 on prod host 192.168.75.105) — fixed by the deployment target.

**Primary Dependencies**:
- Flask (existing) — HTTP routing in `WebServices.py`.
- pymssql / DbConnection helper (existing) — SQL Server access via `static/DbConnection.py`.
- iText 5.5.10 (existing) — PDF layout via the existing PDF helpers in the Flask app.
- matplotlib (new, ~50 MB on disk) — pie/donut, bar, stacked bar, sparkline, US choropleth. Render to in-memory PNG bytes, then embed via iText `Image.getInstance(bytes)`.
- A small US-states GeoJSON asset (~150 KB, checked into the repo) — used by matplotlib's `PathPatch` to draw the choropleth without geopandas.
- threading (stdlib) — single-thread background job + a single module-level lock for the "one at a time" gate.
- smtplib via the existing `sendPRGPDFTo` helper — email delivery.

**Storage**:
- Source: `tblPRGPDFMaster` (PROD Inspection DB, ~11K rows today).
- Mapping: `csi.dbo.AAAFacilities.ClubCode` + `aaastate` (PROD CSI DB, ~250 distinct club rows).
- New audit table: `tblSpecialistsPerfPDFLog` (PROD Inspection DB) — one row per job.
- Generated PDF: written to `/pdfs/` alongside other PDFs the system already produces.

**Testing**: None — Constitution Principle V (No Automated Test Infrastructure). Verification is via manual triggers + recipient inspection of the resulting PDF.

**Target Platform**: Linux x86_64, Python 3.9, Apache 2.4.25 + mod_wsgi 4.7.1. Inside `/var/www/Inspection/WebServices/`. Virtualenv at `/opt/venv3/`.

**Project Type**: Web service (Flask backend on prod). No Android client changes in v1.

**Performance Goals**:
- Trigger acknowledgment: ≤3 s p95 (SC-001).
- End-to-end report delivery: ≤5 min p95 for a 90-day window (SC-002).
- Single PDF size budget: <10 MB so it emails without bounces.

**Constraints**:
- Single in-flight job (FR-014). All concurrent extras are rejected with a 4xx.
- Minimum 28-day window (FR-002).
- No cross-DB join (FR-005) — must do two queries and merge in Python.
- No new Python deps beyond matplotlib (cairosvg was considered and dropped — matplotlib can draw the choropleth from GeoJSON natively).
- Cannot block the Flask request thread during PDF generation; must dispatch to a worker thread.
- mod_wsgi worker reuses code on subsequent requests; module-level state (the in-flight lock) survives between requests within a worker but does NOT survive across worker process restarts. Acceptable for v1.

**Scale/Scope**:
- ~11K source rows today; growing ~30/day.
- ~44 distinct specialists.
- ~250 club codes.
- Single endpoint, single module (~600 LOC est.), single new DB table.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The constitution is Android-app-focused. Five principles, evaluated against this server-side feature:

| Principle | Applies? | Verdict |
|---|---|---|
| I. Fragment-Based Architecture | No | N/A — no Android UI in v1. |
| II. Singleton State Management | No | N/A — Python backend, not Android. |
| III. Flavor-Aware, No Hardcoded Endpoints | **Yes — partial** | The new endpoint lives only on the prod Flask backend per the spec scope. There is no Android caller in v1, so `Constants.kt` does NOT need a URL added. If/when v2 adds an Android trigger, the URL MUST be added to `Constants.kt` and routed via `BuildConfig.FLAVOR`. **Recorded as a future-work note in research.md.** |
| IV. Utility-First (Android Utils) | No | N/A — backend feature. The Python equivalent guidance is followed informally: re-use `sendPRGPDFTo` and `DbConnection` rather than re-implementing. |
| V. No Automated Test Infrastructure | **Yes** | Tasks WILL NOT include unit/integration/contract test tasks. Verification is manual smoke against the live endpoint. |

**Other tech-stack constraints** (Section "Technology Stack Constraints"):
- The constraints (Volley/OkHttp, Glide, MPAndroidChart, etc.) are Android-side. They do not apply to the Python backend. Matplotlib is a Python server dep, NOT an Android dep — does NOT violate the MPAndroidChart constraint.
- iText 5.5.10 is approved for PDF generation; we use it.

**Result**: PASS. No violations to enter into Complexity Tracking.

## Project Structure

### Documentation (this feature)

```text
specs/009-specialists-perf-pdf/
├── plan.md                          # This file
├── research.md                      # Phase 0 — tech decisions, library evaluation, GeoJSON sourcing
├── data-model.md                    # Phase 1 — entities, audit table schema, derived metrics
├── quickstart.md                    # Phase 1 — operator runbook: trigger, observe, debug
├── contracts/
│   └── http-api.md                  # Phase 1 — endpoint contract
├── checklists/
│   └── requirements.md              # Already created by /speckit-specify
└── tasks.md                         # Phase 2 — created by /speckit-tasks (next command)
```

### Source Code (repository root)

```text
web-api-inspectionprod/
├── WebServices.py                                       # MODIFIED: add the new Flask route + dispatch
├── SpecialistsPerfPDFGenerator.py                       # NEW: PDF assembly + chart rendering + email delivery
├── assets/
│   └── us_states.geojson                                # NEW: ~150 KB, used by matplotlib for the choropleth
├── sql/
│   └── 2026-06-24_tblSpecialistsPerfPDFLog.sql          # NEW: audit table DDL
└── static/
    └── DbConnection.py                                  # UNCHANGED: existing helpers reused

web-api-inspectionuat/
└── app/
    └── WebServices.py                                   # MIRROR: same endpoint deployed for parity (per spec scope note)
```

**Structure Decision**: Flask-monolithic. Add one route, one module, one DDL file, one GeoJSON asset. No package restructure. No new directories. Follows the existing layout convention already established by other endpoints in `WebServices.py` (e.g., `/getPDFCompletedVisitations`, `/refreshPrgRspSummary`).

## Complexity Tracking

> No violations. Section intentionally empty.
