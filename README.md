# ClinicManagementWebsite-HealthHub
This is a mini capstone project focuses on a comprehensive management solution for small clinics.

A web-based clinic management platform built using Spring Boot, SQL Server, and Thymeleaf.
The system supports full clinic workflows for patients, doctors, receptionists, technicians, cashiers, and administrators.

II. Installation Guides
1. Environment Setup
Component	Version	Notes
JDK	21	Must be installed and added to PATH.
Spring Boot	3.x.x	Backend framework.
SQL Server	2022	Database engine.
Maven	3.9+	Build and dependency management.
IDE	IntelliJ IDEA / VS Code / Spring Tool Suite	Recommended for development.
Browser	Chrome / Edge	To access the web interface.
2. Project Setup
Clone source code
git clone https://github.com/duc-long/ClinicManagementWebsite-HealthHub.git


Open the project folder in your IDE.

Configure application.properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ClinicDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

spring.thymeleaf.cache=false

server.port=8080

Run the project

Using Maven:

mvn spring-boot:run


Or run the main class directly:

com.healthhub.ClinicManagementApplication

3. Access Application

Open your browser and navigate to:

http://localhost:8080/home

III. User Manual
1. Overview

The Clinic Management System (HealthHub) includes 7 user roles:

Guest: View general information and register as a new patient.

Patient: Book appointments, view test results, manage profile.

Receptionist: Manage appointments, patient check-in, and doctor schedules.

Doctor: View daily appointments, write prescriptions, and update medical records.

Technician: Handle lab tests and upload results.

Cashier: Manage billing and payments.

Admin: Manage users, roles, departments, and clinic-wide configurations.

2. Typical Workflows
Role	Key Features	Steps
Guest	Register new account	Click “Register” → Fill in personal information → Confirm via email OTP
Patient	Book appointment	Login → Appointments → Choose doctor and date → Confirm
Receptionist	Check-in patient	Login → Appointment List → Mark as arrived → Assign to doctor
Doctor	Write medical report	Login → My Schedule → Select patient → Enter diagnosis and prescription
Technician	Handle lab tests	Login → Lab Tests → Update test results
Cashier	Generate and process bill	Login → Payments → Select patient → Confirm amount → Mark as paid
Admin	User and role management	Login → Admin Panel → Create, update, or delete users, roles, or departments