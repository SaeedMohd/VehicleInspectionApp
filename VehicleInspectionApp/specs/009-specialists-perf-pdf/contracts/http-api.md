# HTTP API Contract — Specialists Performance Report PDF

One endpoint. No auth. Mounted on the PROD Inspection Flask backend.

---

## `POST /generateSpecialistsPerfPDF`

### Description

Validates the requested window, acquires the single in-flight job lock, dispatches a background worker, and returns acknowledgment immediately. The worker generates the PDF and emails it to the hardcoded recipient `saeed@pacificresearchgroup.com`.

### Method

`POST` (chosen to express side-effect; no request body is consumed).

### URL parameters

| Param | Type | Required | Example | Notes |
|---|---|---|---|---|
| `from` | `YYYY-MM-DD` | yes | `2026-04-01` | Window start (inclusive). |
| `to` | `YYYY-MM-DD` | yes | `2026-06-30` | Window end (inclusive). |

### Pre-conditions (validated synchronously, in this order)

1. Both `from` and `to` are present.
2. Both parse as `YYYY-MM-DD` strict.
3. `from <= to`.
4. `(to - from).days >= 28`.
5. `(to - from).days <= 1095`.
6. The job lock can be acquired non-blockingly (no other job is running).

### Success response — 202 Accepted

```json
{
  "status": "accepted",
  "recordid": 12345,
  "from": "2026-04-01",
  "to": "2026-06-30",
  "message": "Report queued. PDF will be emailed to saeed@pacificresearchgroup.com when complete."
}
```

`recordid` is the `tblSpecialistsPerfPDFLog.recordid` of the just-inserted `status='running'` row, useful for operator polling.

### Error responses

| HTTP | Body shape | When |
|---|---|---|
| 400 | `{"status":"error","message":"<single-sentence reason>"}` | Any pre-condition 1–5 fails. |
| 409 | `{"status":"busy","message":"A report is already being generated. Try again when the running job completes."}` | Pre-condition 6 fails (lock held). |
| 500 | `{"status":"error","message":"Internal error queueing report."}` | Any unexpected error before the worker is dispatched. |

### Background job behavior (not part of the synchronous response)

1. INSERT `tblSpecialistsPerfPDFLog` row with `status='running'`, `started_at=GETUTCDATE()`.
2. Query CSI for the club→state mapping. (~250 rows.)
3. Query INSPECTION for visits in window. (~thousands of rows.)
4. Aggregate into `OverallAggregate` + per-`SpecialistAggregate`.
5. Render charts in-memory (matplotlib → PNG bytes).
6. Assemble PDF (iText) → write to `/pdfs/SpecialistsPerf_<from>_<to>_<recordid>.pdf`.
7. Send via `sendPRGPDFTo('saeed@pacificresearchgroup.com', filename, 'SpecialistsPerf', full_path)`.
8. UPDATE the log row with `finished_at`, `rows_processed`, `status='ok'`.
9. On any uncaught exception in steps 2–7: UPDATE with `status='failed'`, `error=<first 1000 chars of exception>`.
10. Lock released in `finally` (so even a failed job releases).

### Curl example

```bash
curl -X POST "https://inspection.valueaddedonline.com/generateSpecialistsPerfPDF?from=2026-04-01&to=2026-06-30"
```

### Polling the audit row (operator workflow, not a separate endpoint in v1)

Operators inspect job status via direct DB query on the prod host:

```sql
SELECT TOP 5 recordid, started_at, finished_at, from_date, to_date,
              rows_processed, status, error
FROM dbo.tblSpecialistsPerfPDFLog
ORDER BY recordid DESC;
```

No status-polling HTTP endpoint in v1 (out of scope).

---

## Out of scope for v1 (recap)

- GET endpoint to retrieve a previously-generated PDF.
- Status-polling HTTP endpoint.
- Endpoint to list past jobs.
- Cron-scheduled trigger.
- Per-recipient configurable email.
- Auth header.
