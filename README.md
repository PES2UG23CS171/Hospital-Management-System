# Dhrushaj Hospital — Hospital Management System

[![CI](https://github.com/PES2UG23CS171/Hospital-Management-System/actions/workflows/ci.yml/badge.svg)](https://github.com/PES2UG23CS171/Hospital-Management-System/actions/workflows/ci.yml)

A full-stack Hospital Management System built with **Spring Boot MVC**, **Thymeleaf**, and **MySQL**. It provides a role-aware web dashboard for managing the day-to-day operations of a hospital — patients, doctors, staff, appointments, medicines, medical records, and billing.

Developed by **Dhrushaj Achar**.

**🌐 Live Demo:** [hospital-management-system-production-c65e.up.railway.app](https://hospital-management-system-production-c65e.up.railway.app)

## Features

- **Dashboard** — at-a-glance overview of hospital activity
- **Patient Management** — register, update, and track patients
- **Doctor & Staff Management** — maintain doctor and staff directories
- **Appointments** — schedule and manage patient appointments
- **Medical Records** — maintain per-patient medical history
- **Medicine Inventory** — track medicines and stock
- **Billing** — generate bills with a printable receipt view
- **Admin Panel** — user administration and reports
- **Authentication** — login/registration secured with Spring Security

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 (Web, Data JPA, Security) |
| Frontend | Thymeleaf templates, HTML/CSS |
| Database | MySQL 8 |
| Build | Maven (wrapper included) |
| Deployment | Docker on Railway |

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8 running locally

### Setup

1. Create the database:

   ```sql
   CREATE DATABASE hospital_management;
   ```

2. Copy `.env.example` to `.env` and fill in your database credentials:

   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=hospital_management
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Open [http://localhost:8080](http://localhost:8080) and log in.

### Run with Docker

```bash
docker build -t hospital-management .
docker run -p 8080:8080 --env-file .env hospital-management
```

## Testing

68 tests run against an in-memory H2 database, so the suite needs no local MySQL:

```bash
./mvnw test
```

| Suite | Covers |
|---|---|
| `ManagementApplicationTests` | Context startup, admin seeding, password hashing |
| `SecurityAccessTests` | Public vs. authenticated routes, redirects |
| `RegistrationTests` | Sign-up validation, BCrypt storage, login round-trip |
| `PatientCrudTests` | Patient create / read / update / delete |
| `DoctorAndStaffCrudTests` | Doctor and staff CRUD |
| `MedicineCrudTests` | Medicine CRUD, stock updates, expiry and low-stock queries |
| `BillingAndRecordsTests` | Bill generation, revenue totals, medical records |
| `AppointmentWorkflowTests` | Appointment state machine and role-based transitions |

## Continuous Integration

Every push and pull request to `main` triggers [GitHub Actions](.github/workflows/ci.yml), which runs the
full test suite on JDK 17 and then verifies the Docker image builds.

## Project Structure

```
src/main/java/com/hospital/management/
├── config/        # Security & environment configuration
├── controller/    # MVC controllers per module
├── model/         # JPA entities
├── repository/    # Spring Data repositories
└── service/       # Business logic

src/main/resources/
├── templates/     # Thymeleaf views (per module)
└── static/css/    # Stylesheet
```
