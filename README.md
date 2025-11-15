# Clinic Management System – HealthHub

A web-based clinic management platform built using Spring Boot, SQL Server, and Thymeleaf.
The system provides a complete workflow for clinics including appointment management, medical reports, billing, laboratory operations, and administrative control.

---

## II. Installation Guides

### 1. Environment Setup

| Component     | Version | Notes                                   |
|---------------|---------|------------------------------------------|
| JDK           | 21      | Must be installed and added to PATH.     |
| Spring Boot   | 3.x.x   | Backend framework.                       |
| SQL Server    | 2022    | Database engine.                         |
| Maven         | 3.9+    | Build and dependency management.         |
| IDE           | IntelliJ IDEA / VS Code / STS | Recommended for development. |
| Browser       | Chrome / Edge | To access the web interface.      |

---

### 2. Project Setup

#### Clone Source Code
```
git clone https://github.com/duc-long/ClinicManagementWebsite-HealthHub.git
```

Open the project folder in your preferred IDE.

#### Configure `application.properties`
```
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ClinicDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

spring.thymeleaf.cache=false

server.port=8080
```

#### Run the Project
Using Maven:
```
mvn spring-boot:run
```

Or run the main class directly:
```
com.healthhub.ClinicManagementApplication
```

---

### 3. Access the Application
Open your browser and go to:

```
http://localhost:8080/home
```

---

## III. User Manual

### 1. Overview

The system includes 7 user roles:

- Guest: View general information and register as a new patient.
- Patient: Book appointments, view test results, and manage personal profile.
- Receptionist: Manage appointments, patient check-in, and doctor scheduling.
- Doctor: View assigned appointments, write medical reports, and prescribe medication.
- Technician: Handle laboratory tests and upload results.
- Cashier: Manage billing and payment processes.
- Admin: Full management of users, roles, departments, and clinic data.

---

### 2. Typical Workflows

| Role          | Key Feature                  | Steps                                                                 |
|---------------|------------------------------|------------------------------------------------------------------------|
| Guest         | Register new account         | Register → Fill personal data → Verify via email OTP                  |
| Patient       | Book appointment             | Login → Appointments → Choose doctor/date → Confirm                   |
| Receptionist  | Check-in patient             | Login → Appointment List → Mark as arrived → Assign doctor            |
| Doctor        | Write medical report         | Login → My Schedule → Select patient → Add diagnosis & prescription   |
| Technician    | Handle lab tests             | Login → Lab Tests → Update test results                               |
| Cashier       | Generate bill                | Login → Payments → Select patient → Confirm amount → Mark as paid     |
| Admin         | Manage users & roles         | Login → Admin Panel → Create/Update/Delete users, departments, roles  |

---
