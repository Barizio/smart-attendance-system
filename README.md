# Smart Attendance & Security Management System

A desktop **attendance and access-control application** for organisations, built in Java Swing. Staff sign in via **webcam capture** or **fingerprint**, attendance is logged automatically, and role-based dashboards give supervisors and administrators oversight of who is present. Password reset flows are handled over email with securely hashed credentials.

## ✨ Features

- **Biometric sign-in** — webcam capture and fingerprint-based authentication
- **Automatic attendance logging** — each sign-in is recorded with a timestamp
- **Role-based dashboards** — separate views for supervisors, non-supervisors, IT admins, and directors
- **Secure accounts** — hashed passwords (`PasswordUtil`) and an email-driven password reset flow
- **User sessions** — per-user session handling after login

## 🛠️ Tech Stack

- **Java** (Swing / AWT desktop UI)
- **NetBeans** GUI Builder (`.form` files)
- **Ant** build (`build.xml`)
- Webcam capture + fingerprint integration for biometric sign-in
- Email (SMTP) for password reset notifications

## 🗂️ Project Structure

```
src/saasms/
├── SAASMS.java              # Application entry point
├── Login.java               # Login screen
├── ResetPassword.java       # Email-based password reset
├── FingerprintSignin.java   # Fingerprint authentication
├── Webcam.java              # Webcam capture sign-in
├── PasswordUtil.java        # Password hashing utility
├── EmailSender.java         # SMTP email notifications
├── UserSession1.java        # Session management
├── supervisorDashboard.java # Supervisor view
├── nonsupervisorDashboard.java
├── ITAdmin.java             # Admin management
└── DirectorofServices.java
```

## 🚀 Run Locally

This is a NetBeans Ant project.

**Option A — NetBeans (recommended):**
1. Open NetBeans → *File → Open Project* → select this folder.
2. Right-click the project → *Run* (main class: `saasms.SAASMS`).

**Option B — Ant CLI:**
```bash
ant clean jar
java -jar dist/SAASMS.jar
```

> A webcam is required for webcam sign-in; fingerprint sign-in requires compatible fingerprint hardware/driver.

## 🔗 Live Demo / Screenshots

<!-- Add screenshots of the login, dashboards, and biometric sign-in screens here -->
_Add screenshots of the login and dashboard screens here._

## 📄 License

MIT
