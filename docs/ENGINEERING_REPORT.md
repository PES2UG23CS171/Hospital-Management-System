# Hospital Management System — Engineering Report

**Author:** Dhrushaj Achar
**Date:** July 2026
**Repository:** https://github.com/PES2UG23CS171/Hospital-Management-System

---

## 1. Project summary

A role-aware web dashboard covering the daily operations of a hospital, built with **Spring Boot 3.2** (Spring MVC, Spring Data JPA, Spring Security), **Thymeleaf** server-rendered views, and a **MySQL**-compatible database.

| Metric | Value |
|---|---|
| Lines of Java (main) | 1,731 |
| JPA entities | 9 |
| Controllers | 10 |
| Services | 10 |
| Repositories | 9 |
| Thymeleaf views | 22 |
| Tests | 68 across 8 suites |

Nine entities model the domain — patients, doctors, staff, appointments, medical records, medicines, bills, admins, and users — each with a repository, a service holding business logic, and a controller exposing the screens. Five roles (`ADMIN`, `DOCTOR`, `STAFF`, `RECEPTIONIST`, `PATIENT`) determine what a signed-in user can reach.

Two areas carry real logic rather than plain persistence:

**Appointment lifecycle** — an enforced state machine:

```
REQUESTED → SCHEDULED → CHECKED_IN → IN_CONSULTATION → COMPLETED
                                          ↓
                                    TESTS_ORDERED
```

Transitions are guarded inside the entity: an appointment cannot be checked in before it is scheduled, cannot be completed directly from `REQUESTED`, and cannot be cancelled once `COMPLETED`. Completing a consultation automatically generates the patient's bill.

**Medicine inventory** — records derive their own status, flagging themselves `OUT_OF_STOCK` or `EXPIRED` when stock is checked, with queries for low stock and upcoming expiry.

---

## 2. Changes made

Seven commits took the project from its original coursework state to its current one.

### 2.1 Repository hygiene

- A `.env` file containing database credentials and an application password had been committed. It was removed from the working tree **and from every commit in history** by rebuilding the repository on a single authored commit and force-pushing.
- Added `.env.example` with placeholder values, and `.gitignore` entries for `.env` and `.DS_Store`.
- Replaced placeholder branding across 20 templates.
- Rewrote the README, which was previously an unreadable UTF-16 file containing only the repository name.

### 2.2 Missing feature: user registration

The login page linked to `/register`, but there was no page, no endpoint, and Spring Security blocked the URL. The only way in was a single hardcoded administrator account.

Implemented end to end:

- A registration page matching the login page's design, with a role selector.
- A `POST /register` endpoint validating username length, password strength, password confirmation, and duplicate accounts — each with a specific error message.
- A `CustomUserDetailsService` so Spring Security authenticates against the `users` table.
- **BCrypt** password hashing; plaintext passwords are never stored.
- Server-side rejection of self-registration as `ADMIN`.
- A `DataInitializer` that seeds the admin account from environment variables on first boot.

### 2.3 Deployment readiness

- All environment-specific configuration (database host, port, name, credentials, HTTP port, admin password) moved to environment variables with local defaults, so one build runs everywhere.
- Multi-stage `Dockerfile`: Maven build stage, then a JRE-only runtime image.
- JVM heap capped so the container fits a 512 MB free-tier instance.
- Hikari connection pool drains idle connections after 60 seconds, allowing the service to scale to zero when unused.
- Database migrated to a free MySQL-compatible cloud tier, independent of the application host.

### 2.4 Quality: tests and CI

Covered in sections 3 and 4.

---

## 3. Test suites

The suite runs against an **in-memory H2 database** in MySQL compatibility mode. Two consequences matter: the tests need no MySQL installation, and they can never reach the production database. Each test class runs in a transaction that rolls back afterwards, so tests cannot leak state into one another.

| Suite | Tests | Covers |
|---|---:|---|
| `SecurityAccessTests` | 16 | Public vs. protected routes, redirects, view resolution |
| `RegistrationTests` | 11 | Sign-up validation, BCrypt storage, login round-trip |
| `AppointmentWorkflowTests` | 11 | Lifecycle transitions, rejected moves, role permissions |
| `MedicineCrudTests` | 8 | Inventory CRUD, stock updates, expiry and low-stock queries |
| `BillingAndRecordsTests` | 7 | Bill creation, revenue totals, receipts, medical records |
| `PatientCrudTests` | 6 | Patient create, list, edit, update, delete |
| `DoctorAndStaffCrudTests` | 6 | Doctor and staff directories, specialization queries |
| `ManagementApplicationTests` | 3 | Context startup, admin seeding, password hashing |
| **Total** | **68** | Runs in roughly 20 seconds locally |

### What the tests actually assert

The CRUD tests drive real HTTP requests through the full Spring stack — security filters, controllers, services, repositories — then verify the database, rather than treating a redirect as proof of success.

The security tests confirm ten protected routes redirect anonymous visitors, and that role restrictions hold: a receptionist may schedule an appointment, a patient receives `403 Forbidden`.

The registration tests assert that a stored password is **not** the plaintext that was typed, that it verifies against BCrypt, and that a newly registered account can genuinely authenticate. A password-hashing regression produces no visible symptom until an account is compromised, which is exactly why it needs a test.

### A real defect, found on the first run

Two controllers both mapped the root URL `/` — `RootController` redirecting to login, and `DashboardController` serving the dashboard. Spring resolves this at request time rather than at startup, so the application booted cleanly and the bug stayed invisible: logging in redirects straight to `/dashboard`, so manual clicking never touched `/`.

A signed-in user visiting the bare domain received a **500 error**. The security test requesting `/` as an authenticated user failed immediately:

```
java.lang.IllegalStateException: Ambiguous handler methods mapped for '/':
  {DashboardController.dashboard(Model), RootController.root()}
```

The redundant controller was removed. The root path now serves the dashboard to signed-in users and redirects everyone else to login.

---

## 4. Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request targeting `main`, and can be triggered manually. Two jobs, the second gated on the first:

**Job 1 — Build & Test**

1. Check out the pushed commit
2. Set up JDK 17 (Temurin) with the Maven cache restored
3. `./mvnw --batch-mode verify` — the build fails on any failing test
4. Upload test reports as an artifact (`if: always()`, so failures are diagnosable without re-running locally)

**Job 2 — Docker Image Builds**

Builds the container image, proving the deployment path still works.

The Docker stage is the part that pays for itself least visibly: tests can pass while the image build is broken — a missing file, a bad base image, a dependency that resolves locally but not in a clean environment. Catching that in CI is the difference between a red mark on a commit and a demo that is down when someone opens it.

The whole pipeline completes in roughly **80 seconds** from push to green.

---

## 5. Benefits

### Before and after

| Before | After |
|---|---|
| One placeholder test asserting only that the context loads | 68 tests across security, CRUD, validation, and workflow logic |
| No way to know a change broke something until a page was clicked | Every push checked automatically in about 80 seconds |
| Credentials committed in version control history | Clean history; secrets supplied through the environment |
| Registration advertised in the interface but not implemented | Working registration with hashed passwords and role control |
| Configuration hardcoded to one machine | One build that runs identically locally, in CI, and in production |
| Deployment breakage discovered only after a failed deploy | Container build verified before the platform sees it |

### Practical benefits

- **Regressions surface immediately.** With ten controllers sharing the same services and security configuration, a change in one module routinely breaks another. The suite turns that from a user discovery into a red mark on a commit.
- **Security properties stay true.** Route protection, password hashing, and role enforcement are asserted rather than assumed — precisely the behaviours that decay quietly during refactoring, because nothing visibly breaks when they do.
- **The deployment stays deployable.** Building the image on every push means the live demo does not go dark because of a change that looked harmless.
- **Refactoring becomes safe.** Removing the duplicate root controller changed shared routing. Making that change confidently was only possible because 68 tests confirmed nothing else moved.
- **The tests document the system.** The appointment suite is the clearest available description of which transitions are legal — more precise than a comment, and it cannot fall out of date without failing.
- **Visible evidence of practice.** A passing badge on a public repository tells a reviewer the project is tested and the tests pass, before they read any code.

The most useful outcome was not the test count. It was that the first complete run found a defect that was live in production and invisible to manual use — the argument for automated testing, demonstrated on the project's own code.
