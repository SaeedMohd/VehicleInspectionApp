# Quickstart — Specialists Performance Report PDF

Operator runbook. Assumes the feature is deployed to prod.

---

## Fire a report

From any host with VPN access to `inspection.valueaddedonline.com`:

```bash
curl -X POST 'https://inspection.valueaddedonline.com/generateSpecialistsPerfPDF?from=2026-04-01&to=2026-06-30'
```

Expected synchronous response (~1 second):

```json
{
  "status": "accepted",
  "recordid": 1234,
  "from": "2026-04-01",
  "to": "2026-06-30",
  "message": "Report queued. PDF will be emailed to saeed@pacificresearchgroup.com when complete."
}
```

PDF arrives at `saeed@pacificresearchgroup.com` within ~5 minutes for a 90-day window.

---

## Check job status

SSH to the prod host and run:

```bash
ssh smostafa@192.168.75.105 "/opt/venv3/bin/python3 -c \"
import sys; sys.path.insert(0,'/var/www/Inspection/WebServices')
from static.DbConnection import DbConnection
print(DbConnection.queryDb('SELECT TOP 5 recordid, started_at, finished_at, from_date, to_date, rows_processed, status, error FROM dbo.tblSpecialistsPerfPDFLog ORDER BY recordid DESC'))
\""
```

Status values: `running`, `ok`, `failed`. A `failed` row carries an `error` field that tells you what went wrong.

---

## Common errors

| Symptom | Cause | Fix |
|---|---|---|
| `400 Window must be at least 28 days` | Window < 28 days | Use a wider date range. |
| `400 Maximum window is 3 years` | Window > 1095 days | Split into multiple reports. |
| `400 from must be YYYY-MM-DD` | Bad date format | Use ISO format, e.g. `2026-04-01`. |
| `409 A report is already being generated.` | Another job in flight | Wait for the running job's email to arrive, then retry. Check the latest `running` row in the audit table to see how long it has been going. |
| PDF never arrives, audit row shows `status='failed', error='...'` | Job crashed mid-render or SMTP exhaustion | Inspect the `error` column. If matplotlib/iText error, may indicate a data issue (e.g. specialist name with corrupt unicode). If SMTP, retry the same window; mail relay was transiently down. |
| PDF never arrives, audit row shows `status='running'` for >10 min | mod_wsgi worker likely restarted mid-job, leaving an orphan row | Manually mark it as aborted: `UPDATE dbo.tblSpecialistsPerfPDFLog SET status='failed', error='orphaned', finished_at=GETUTCDATE() WHERE recordid=<N> AND status='running'`. Then retry. |

---

## Inspect the generated PDF

Generated PDFs are saved under `/pdfs/` on the prod host with a deterministic filename:

```text
/pdfs/SpecialistsPerf_<from>_<to>_<recordid>.pdf
```

You can re-download a past PDF without re-running the job:

```bash
ssh smostafa@192.168.75.105 "ls -lh /pdfs/SpecialistsPerf_*.pdf | tail"
scp smostafa@192.168.75.105:/pdfs/SpecialistsPerf_2026-04-01_2026-06-30_1234.pdf .
```

---

## Deploy procedure (one-time + redeploy)

### Initial deployment

1. Apply DDL on prod Inspection DB:
   ```bash
   ssh smostafa@192.168.75.105 "/opt/venv3/bin/python3 -c \"
   import sys; sys.path.insert(0,'/var/www/Inspection/WebServices')
   from static.DbConnection import DbConnection
   print(DbConnection.updateDB(open('/tmp/2026-06-24_tblSpecialistsPerfPDFLog.sql').read()))
   \""
   ```
2. `scp` `SpecialistsPerfPDFGenerator.py` and `assets/us_states.geojson` to prod.
3. `scp` updated `WebServices.py` to prod.
4. Install matplotlib in the prod venv (one-time):
   ```bash
   ssh smostafa@192.168.75.105 "echo 'rAG5gREqRx8xtQLS' | sudo -S /opt/venv3/bin/pip install matplotlib"
   ```
5. Reload Apache:
   ```bash
   ssh smostafa@192.168.75.105 "echo 'rAG5gREqRx8xtQLS' | sudo -S systemctl reload apache2"
   ```
6. Smoke-test with the curl above. Expect `202` in ~1 s; PDF in inbox in ~5 min.

### Redeploy (code change only)

Steps 2, 3, 5. Verify with curl.

---

## Verification checklist (post-deploy)

- [ ] `curl` returns `202` in under 3 seconds.
- [ ] Audit row appears with `status='running'`.
- [ ] PDF arrives at the recipient within 5 minutes (90-day window).
- [ ] Audit row transitions to `status='ok'` with `rows_processed` matching `SELECT COUNT(*) FROM tblPRGPDFMaster WHERE performeddate BETWEEN ...`.
- [ ] Second `curl` fired while first is running returns `409`.
- [ ] `curl` with `from=2026-06-01&to=2026-06-15` (14 days) returns `400 Window must be at least 28 days`.
- [ ] PDF dashboard's KPI tiles reconcile to the audit row's `rows_processed` (in-person count + waived count + everything else).
- [ ] Choropleth top-3 states render with labels.
