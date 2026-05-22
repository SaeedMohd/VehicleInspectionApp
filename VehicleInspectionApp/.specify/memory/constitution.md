<!--
SYNC IMPACT REPORT
==================
Version change: 0.0.0 (uninitialized template) → 1.0.0
Modified principles: All (initial population from template placeholders)
Added sections:
  - Core Principles (5 principles derived from CLAUDE.md + codebase)
  - Technology Stack Constraints
  - Build & Deployment Workflow
  - Governance
Removed sections: None (template comments stripped)
Templates requiring updates:
  - .specify/templates/plan-template.md ✅ (Constitution Check gate references these principles)
  - .specify/templates/spec-template.md ✅ (no structural changes required; Android context aligns)
  - .specify/templates/tasks-template.md ✅ (no test tasks by default — matches Principle V)
Deferred TODOs: None
-->

# VehicleInspectionApp Constitution

## Core Principles

### I. Fragment-Based Architecture (NON-NEGOTIABLE)

All new UI features MUST be implemented as Fragments hosted in the appropriate existing Activity
(MainActivity, FormsActivity, or ApplicantActivity). Direct Activity-to-Activity navigation for
new flows is prohibited. Fragment transactions MUST follow the existing manual transaction pattern
already established in the codebase. The Navigation Component dependency is present but unused;
it MUST NOT be introduced for new flows without a constitution amendment.

### II. Singleton State Management

Application state MUST be stored and retrieved through the existing global singletons:
`FacilityDataModel` for facility data, `ApplicationPrefs` for session/user/cached data, and
`AnnualVisitationSingleton` for annual visitation state. New ViewModel or LiveData classes are
NOT to be introduced. Any new persistent data key MUST be added to `ApplicationPrefs.java` using
the existing accessor pattern.

### III. Flavor-Aware, No Hardcoded Endpoints

All API communication MUST route through the URL selected at runtime via `BuildConfig.FLAVOR` in
`Utils/Constants.kt`. No URL strings are permitted outside Constants.kt. Every code path that
differs between UAT and Production MUST use `BuildConfig.FLAVOR` checks or flavor-specific
resource files — never inline string comparisons.

### IV. Utility-First — No Duplication

Before writing any new helper function, `Utils/Utils.kt` (~7K lines) and `Utils/Utility.java`
(~49K lines) MUST be checked for an existing solution. New utility code MUST be added to
`Utils/Utils.kt` (Kotlin preferred for all new code). Java utilities are legacy; new
Kotlin equivalents are preferred when extending functionality.

### V. No Automated Test Infrastructure

The project has no test dependencies, no test directories, and no test runner configuration.
Test-driven development is NOT required. Feature verification happens through device testing
on debug builds and manual QA. Tasks MUST NOT include unit/integration/contract test tasks
unless the project constitution is amended to add testing infrastructure.

## Technology Stack Constraints

All networking MUST use Volley or OkHttp; Retrofit is not in the dependency graph and MUST NOT
be added without a constitution amendment. New background operations MUST follow the existing
`AsyncTask` pattern in `serverTasks/` (29 existing task classes serve as reference). Coroutines
and RxJava are NOT approved.

Image loading MUST use Glide. Maps features MUST use the Google Maps SDK. Crash reporting MUST
go through both Firebase Crashlytics and Bugfender (flavor-specific keys in `CustomApplication`).
PDF generation uses iText 5.5.10; PDF viewing uses android-pdf-viewer. Chart rendering uses
MPAndroidChart. Signature capture uses the simplify/ink library. Multi-step forms use
vertical-stepper-form. No alternative libraries for these capabilities may be introduced without
a constitution amendment and a documented justification.

## Build & Deployment Workflow

Release builds MUST be signed with `VehicleHealthMonitor.keystore` (configured in
`app/build.gradle`). ProGuard minification is enabled for all release build types; any new
class that must survive minification MUST have a corresponding ProGuard rule in
`proguard-rules.pro`. UAT flavor builds are used for QA; Production flavor builds are used for
distribution. Build commands are documented in `CLAUDE.md` and MUST remain accurate there.
Cleartext HTTP is permitted only for domains listed in `network_security_config.xml`; adding new
cleartext exceptions requires explicit justification in the PR description.

## Governance

This constitution supersedes all other development practices within this repository. All feature
work and code reviews MUST verify compliance with these principles before merge. Violations that
are technically necessary MUST be documented in the feature plan's Complexity Tracking table with
a clear rationale and confirmation that simpler alternatives were considered and rejected.

Amendments to this constitution require: (1) a written rationale, (2) updating
`LAST_AMENDED_DATE` and incrementing `CONSTITUTION_VERSION` per semantic versioning, and (3)
propagating any impacted changes to `.specify/templates/` files as documented in the Sync Impact
Report embedded at the top of this file.

Versioning policy: MAJOR for principle removals or redefinitions; MINOR for new principles or
materially expanded guidance; PATCH for clarifications and wording fixes.

**Version**: 1.0.0 | **Ratified**: 2026-05-15 | **Last Amended**: 2026-05-15
