# Feature Specification: Specialists Performance Report PDF

**Feature Branch**: `009-specialists-perf-pdf`

**Created**: 2026-06-24

**Status**: Draft

**Input**: User description: "Specialists Performance Report PDF — on-demand HTTP endpoint that generates a PDF report summarising AAA Specialists' visitation performance from `tblPRGPDFMaster` (PROD Inspection DB), runs as a background job, and emails the finished PDF to `saeed@pacificresearchgroup.com`. PDF has a Summary dashboard with KPI tiles, donut + bar charts, a full-page US choropleth map, plus a Per-Specialist section with a leaderboard table and one mini-card per specialist. Date range supplied as `from` / `to`. No per-visit listings, no configurable targets, no scheduled generation, no Android UI for v1."

## Clarifications

### Session 2026-06-24

- Q: How is the trigger endpoint authenticated? → A: No auth — same posture as every other endpoint already on this backend.
- Q: What is the concurrency model for the trigger? → A: Exactly one job at a time. Any trigger arriving while a job is running is rejected immediately; nothing is queued.
- Q: How should the per-specialist sparkline be bucketed when the window is shorter than a month? → A: The window can't be shorter than a month — reject any trigger where `to - from` is less than 28 days. Sparkline stays monthly per FR-011.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - On-demand Performance Report for a Date Range (Priority: P1)

A program director needs a snapshot of how the field specialists performed across a specific time window (last quarter, last month, calendar year). The director triggers a single request supplying the date window. The system acknowledges the request immediately so the director isn't blocked. A few minutes later, the director receives an emailed PDF that opens with a one-page dashboard of the headline numbers and a US map showing where the work happened, followed by a roster of every specialist who logged at least one visit in that window, ranked by volume.

**Why this priority**: This is the entire feature. Without it there is no report. Without on-demand triggering the director cannot answer ad-hoc business questions ("how did we do in May?", "show me Q2").

**Independent Test**: Trigger the report for a known date range (e.g., 2026-04-01 to 2026-06-24). Within five minutes the configured recipient receives an email with the PDF attached. The PDF opens, contains the dashboard, the choropleth map, and the leaderboard, and the numbers reconcile against a hand-run aggregation of the underlying table.

**Acceptance Scenarios**:

1. **Given** the system is healthy and the source data contains visits in the requested window, **When** the director triggers a report for `from=2026-04-01&to=2026-06-30`, **Then** the trigger is acknowledged in under 3 seconds and a PDF email arrives at the configured recipient within 5 minutes.
2. **Given** a report has been generated, **When** the recipient opens the PDF, **Then** the cover page shows the exact date range supplied, a timestamp of when the report was generated, the total visit count, and the count of distinct specialists who contributed visits.
3. **Given** a report has been generated, **When** the recipient reaches the dashboard page, **Then** the five KPI tiles, two donuts, and four bar charts all reflect the same underlying visit set, with all percentages summing to 100% within the rounding tolerance of one percentage point.
4. **Given** a report has been generated, **When** the recipient reaches the US map page, **Then** every state in which at least one club operated during the window is shaded, the shading intensity reflects that state's share of total visits, and the three states with the highest share are labeled with their percentage.

---

### User Story 2 - Per-Specialist Leaderboard and Snapshot (Priority: P2)

The director uses the report to understand each specialist's individual performance — not their day-by-day work, but their overall mix and volume. The director scans a single-page leaderboard table to spot outliers (someone with very high waived %, someone with very low in-person %, someone covering many facilities, someone covering few), then flips through compact one-third-page snapshots per specialist to confirm what they saw in the leaderboard.

**Why this priority**: This is the "who" view that turns the headline numbers from Story 1 into individual accountability. Story 1 delivers the aggregate picture; Story 2 makes it actionable.

**Independent Test**: For a known specialist and date range, verify that the leaderboard row's visit count, in-person %, and waived % match a hand-aggregation of the underlying rows. Verify that the same specialist's snapshot card shows the same totals plus a method-mix donut and a type-mix donut whose slice percentages also reconcile.

**Acceptance Scenarios**:

1. **Given** the report has been generated for a window where N specialists have at least one visit, **When** the recipient reaches the leaderboard page, **Then** the table shows exactly N rows, sorted by visit count descending, each row exposing the columns Specialist, Visits, In-Person %, Waived %, Annual, Quarterly, AdHoc, Deficiency, Facilities, Clubs.
2. **Given** the leaderboard exceeds the page height, **When** the report is rendered, **Then** the leaderboard paginates without truncating any specialist row, and pagination preserves the descending sort order.
3. **Given** the per-specialist mini-card section, **When** the recipient flips through it, **Then** each card shows the specialist's name, total visit count, method-mix donut, type-mix donut, In-Person %, Waived %, facility count, and a monthly visit sparkline that aligns to the report's date range.
4. **Given** a specialist contributed zero visits in the requested window, **When** the report is rendered, **Then** that specialist appears in neither the leaderboard nor the mini-card section.

---

### User Story 3 - Reliable Job Tracking and Failure Visibility (Priority: P3)

When the director triggers a report and it doesn't arrive, someone on the operations side needs to determine whether the job ran, whether it failed, what it failed on, and whether a retry is safe — without restoring from logs or guessing.

**Why this priority**: Operational hygiene. Without it, a missed email looks identical to a missed click, and there is no way to know whether a re-trigger will succeed or compound the problem.

**Independent Test**: Trigger a report against a deliberately broken configuration (e.g., bad recipient address surrogate, or insert a poison row). Verify the failure is recorded in the job log with status, error text, and the date window, and that a second trigger using the same window starts a fresh job rather than reusing the failed one.

**Acceptance Scenarios**:

1. **Given** any report trigger, **When** the job starts, **Then** an audit record exists with start time, the requested date window, and status `running`.
2. **Given** the job completes successfully, **When** the email is sent, **Then** the audit record is updated to status `ok` with finish time and the count of visit rows it processed.
3. **Given** the job fails, **When** the failure surfaces, **Then** the audit record is updated to status `failed` with finish time and a short error description that an operator can act on.
4. **Given** a job is already running, **When** a second trigger arrives (same window or different window), **Then** the second trigger is rejected synchronously with a "report already in progress" response, no second audit row is created, and the running job is unaffected.

---

### Edge Cases

- **Empty window**: The requested `from` / `to` range contains zero visit rows. The report is still generated but the dashboard, map, leaderboard, and per-specialist section all show an explicit "no visits in period" message instead of empty charts.
- **Invalid date arguments**: `from` is after `to`, either parameter is missing, either parameter is not a parseable ISO date, or the window is shorter than 28 days. The trigger is rejected synchronously with a clear error and no audit record is created.
- **Wide-but-bounded window**: A window approaching the 3-year hard cap produces a leaderboard or mini-card section that may exceed 10 pages. The PDF still renders without crashing, paginates cleanly, and is delivered. Windows beyond the 3-year cap are rejected synchronously (FR-002).
- **Specialist name with unusual characters**: Apostrophes, accented characters, or unusually long names render correctly on cover, leaderboard, and mini-card without overflowing column boundaries.
- **Club without state mapping**: A `clubcode` appears in the visit data that has no entry in the `ClubCode → state` reference. Those visits are still counted in the totals and the leaderboard but are excluded from the US map's state coloring; the map page footnotes the count of unmapped visits so the recipient is aware.
- **Email delivery transient failure**: SMTP rejects the message on first attempt. The system retries delivery, and only after retries are exhausted does the job mark itself failed.
- **Concurrent triggers**: A second trigger arrives while a job is running. The second trigger is rejected immediately with a clear "report already in progress" response. The running job is unaffected; the operator can retry after the running job emails its PDF.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST expose a single trigger that accepts a `from` date and a `to` date and acknowledges the request without waiting for report generation to finish.
- **FR-002**: System MUST reject the trigger synchronously when `from` is missing, `to` is missing, either is not a parseable ISO date, `from` is after `to`, or the window spans fewer than 28 days (the minimum reporting period is one month).
- **FR-003**: System MUST run the report generation as a background job so the trigger can be acknowledged in under 3 seconds.
- **FR-004**: System MUST source visit data exclusively from `tblPRGPDFMaster` in the PROD Inspection database, using only the columns `performeddate`, `specialist`, `visitationmethod`, `visitationtype`, `visitationreason`, `waivevisitation`, `facno`, `clubcode`.
- **FR-005**: System MUST source the `ClubCode → state` mapping from the CSI database's `AAAFacilities` table at the start of each job and reuse the resolved mapping for both the leaderboard's clubs/states columns and the US choropleth, without performing a cross-database join during data extraction.
- **FR-006**: System MUST filter the visit data by `performeddate` ∈ [`from`, `to`] inclusive, using calendar dates (the `to` date includes the full day).
- **FR-007**: System MUST produce a PDF whose first page is a cover showing report title, date range, generation timestamp, total visit count, and active-specialist count.
- **FR-008**: System MUST produce a Summary Dashboard page exposing exactly five KPI tiles — Total Visits, Active Specialists, Facilities Covered, In-Person %, Waived % — plus six charts: Method donut, Type donut, Visits per Month vertical bar, Performed-vs-Waived stacked monthly bar, Top 10 Visit Reasons horizontal bar, Top 10 Clubs horizontal bar.
- **FR-009**: System MUST produce a full-page US map shading each state by its share of total visits, with the three highest-share states labeled with their percentage and a footnote disclosing the count of any visits whose club has no state mapping.
- **FR-010**: System MUST produce a Leaderboard page (or pages) with one row per specialist who has at least one visit in the window, sorted by visit count descending, exposing the columns Specialist, Visits, In-Person %, Waived %, Annual, Quarterly, AdHoc, Deficiency, Facilities, Clubs.
- **FR-011**: System MUST produce a Per-Specialist section with one mini-card per specialist, three cards per page, each card showing name, total visits, method-mix donut, type-mix donut, In-Person %, Waived %, facility count, and a monthly sparkline aligned to the report's date range.
- **FR-012**: System MUST email the finished PDF as an attachment to the configured recipient. For v1 this recipient is hardcoded as `saeed@pacificresearchgroup.com`.
- **FR-013**: System MUST record an audit row per job, capturing start time, finish time (when applicable), the requested `from` / `to`, count of rows processed, status (`running`, `ok`, `failed`), and a short error description for failures.
- **FR-014**: System MUST allow only one report job to be in flight at a time. Any trigger arriving while a job is running MUST be rejected synchronously with a clear "report already in progress" response. The rejected trigger MUST NOT create an audit row, queue, or block.
- **FR-015**: System MUST render the report at A4 landscape with the existing application brand masthead colors so it visually matches other PDFs emitted by the same system.
- **FR-016**: System MUST render the cover, dashboard, leaderboard, map, and per-specialist sections even when the window contains zero visits, replacing chart bodies and table rows with an explicit "no visits in period" message.
- **FR-017**: System MUST retry transient email delivery failures before marking a job failed; sustained delivery failure MUST flow into the audit row's error description.
- **FR-018**: The trigger endpoint MUST NOT require authentication or authorization — it inherits the same open-access posture as every other endpoint on the PROD Inspection backend. The endpoint is reachable only on the internal network, which is the v1 security boundary.

### Key Entities *(include if feature involves data)*

- **Visit**: One row in `tblPRGPDFMaster` representing a single completed visitation. Used attributes: when it was performed, which specialist did it, the method (In Person / Phone / Email / Web Conference), the type (Annual / Quarterly / AdHoc / Deficiency), the human-readable reason, whether it was waived, the facility number, and the club code.
- **Specialist**: A field employee, identified by name. Aggregated from the `specialist` column of Visit rows. A specialist is "active in the window" iff they have at least one Visit row in the window.
- **Club → State Mapping**: A lookup from `clubcode` to a US state postal code. Sourced from the CSI database at job start, snapshotted in memory for the duration of the job.
- **Report Job**: A single invocation of the trigger. Has a unique identifier, a date window, a status lifecycle (`running` → `ok` or `failed`), start and finish timestamps, the count of source rows it processed, and an error description for failures.
- **Report PDF**: The artifact produced by a successful Report Job. Stored alongside other generated PDFs and delivered via email.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A trigger for any valid `from` / `to` window returns acknowledgment to the caller in under 3 seconds, measured at the 95th percentile across at least 10 trials.
- **SC-002**: For a 90-day window covering the full active specialist roster, the finished PDF arrives in the recipient inbox within 5 minutes of the trigger, measured at the 95th percentile across at least 5 trials.
- **SC-003**: All percentages reported in the PDF (KPI tiles, donuts, leaderboard columns, map shading) reconcile to within ±1 percentage point of a hand-computed aggregation of the source data for any spot-checked specialist and any spot-checked state.
- **SC-004**: For a window with zero visits, the PDF is delivered with all sections present and the recipient can understand from the report alone that the window was empty, without needing to consult a separate explanation.
- **SC-005**: Two triggers fired within 10 seconds of each other result in exactly one delivered PDF and exactly one audit row; the second trigger receives an immediate "report already in progress" response and does not create or update any audit row.
- **SC-006**: When the email recipient receives a PDF, they can identify each specialist's relative performance (visit volume, in-person mix, waived rate) within 30 seconds of opening the report, without consulting any external reference. *Qualitative — verified post-launch by stakeholder feedback, not by a smoke task.*
- **SC-007**: The US map page makes the geographic distribution of work obvious at a glance — within 10 seconds of opening that page, the recipient can correctly name the top-volume state. *Qualitative — verified post-launch by stakeholder feedback, not by a smoke task.*
- **SC-008**: When a job fails, an operator inspecting the audit row alone (no log files, no email forensics) can identify the date window, the failure point, and decide whether to retry — in under 60 seconds.

## Assumptions

- The report is consumed only by internal recipients; no external party receives the PDF in v1.
- The recipient address `saeed@pacificresearchgroup.com` is correct and stable for the v1 lifetime of the feature.
- "All specialists with ≥1 visit in period" means the de-duplicated set of `specialist` values appearing in `tblPRGPDFMaster` rows that fall in the window — there is no separate authoritative specialist roster to reconcile against.
- The CSI `AAAFacilities` snapshot taken at job start is acceptable as the `clubcode → state` source even if it changes during job execution (jobs are short-lived enough that intra-job drift is negligible).
- AAA visitation methods are limited to the four already present in the underlying data: In Person, Phone, Email, Web Conference. Any new method that appears later will surface in the dashboard but is out of scope for v1 styling.
- AAA visitation types are limited to the four already present: Annual, Quarterly, AdHoc, Deficiency. Same caveat as method.
- The existing internal mail relay used by other PDF emails from this backend is the delivery channel for v1.
- "Background job" means in-process — running asynchronously inside the same backend process that accepted the trigger. There is no separate worker process, queue daemon, or external orchestrator in v1.
- v1 is delivered to production first. A UAT mirror exists for parity but the trigger is not exposed there for v1.
- No Android-side UI surfaces this feature in v1; the trigger is fired by an operator with shell or HTTP access.
