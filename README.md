# 🎓 Student Organizer

An all-in-one Android application designed to help students manage courses, track assignments, log attendance, organize study notes, and schedule academic events seamlessly.

[![Android SDK](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.1.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room DB](https://img.shields.io/badge/Database-Room%202.8.5-orange.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

---

## 🌟 Overview

**Student Organizer** empowers students to stay ahead of their academic responsibilities through a clean, intuitive, modern Android application. Built using modern Android development practices, it features a hybrid architecture combining **Jetpack Compose Material 3** dialogs/bottom sheets with **Android ViewBinding** and **ViewPager2** tabbed navigation for a performant and responsive user experience.

---

## 📌 Problem Statement

### **Title**: *Fragmented Academic Task and Attendance Management for Higher Education Students*

### **The Challenge**:
College and university students consistently struggle with academic fragmentation and time management. Modern academic life requires managing multiple courses, assignment deadlines, exam dates, study materials, and minimum attendance requirements across separate tools.

### **Key Pain Points**:
1. **App Switching & Cognitive Overload**: Students rely on 4–5 separate applications (a generic calendar, a task manager, a note-taking app, an attendance counter, and photo galleries for lecture notes), leading to clutter and missed deadlines.
2. **Attendance Risk Ignorance**: Most academic institutions enforce a strict attendance threshold (e.g., 75%). Generic calendar apps do not compute attendance percentages or warn students before they fall into a critical attendance shortage.
3. **Disorganized Study Materials**: Notes and assignment tasks are rarely categorized by specific courses, making exam preparation inefficient.
4. **Dependence on Network Connectivity**: Many student planner apps depend heavily on server connections or cloud sync, causing sluggish performance or unavailability in offline classrooms/libraries.

---

## ✨ Unique Selling Propositions (USP)

#### 🟢 **1. Gesture-Driven Attendance Logger & Smart Risk Calculator**
* Instead of manual counter buttons, the app features an interactive Jetpack Compose calendar where students can use multi-tap gestures (**Tap / Double-tap / Triple-tap**) directly on dates to mark attendance (**Present 🟢** vs **Absent 🔴**).
* Automatically calculates real-time overall attendance percentages and alerts students if their attendance drops below safety thresholds (e.g., < 75%).

#### 🔗 **2. Dynamic Contextual Course Linking**
* Every component in the app is interconnected:
  * Viewing a course shows its **live assignment progress bar** (e.g., 3/5 completed).
  * Tapping *"View Study Materials"* on a course automatically filters and opens all study notes tagged with that course code.

#### ⚡ **3. Hybrid Architecture (Native Views + Jetpack Compose M3)**
* Bridges modern Jetpack Compose Material 3 sheets/dialogs with high-performance ViewPager2 tab navigation. This offers fluid 60fps swipe navigation across tabs while keeping dialog interactions modern, animated, and responsive.

#### 🔒 **4. 100% Local & Offline-First Privacy**
* Built entirely on **Room ORM (SQLite)** with **KSP**. Student data (grades, notes, attendance, schedule) remains strictly on the device—no forced cloud logins, zero tracking, and instant sub-millisecond response times even without an internet connection.

#### 🎯 **5. Priority-Based Task & Progress Tracking**
* Assignments are classified not only by status (*Pending, In Progress, Completed*) but also by visual priority (*High*, *Medium*, *Low*), letting students instantly identify urgent coursework.

---

## ✨ Key Features

### 📊 1. Dashboard & Quick Overview
- **Real-time Overview Metrics**: Displays pending assignment counts, overall attendance percentage, upcoming exams, and active courses at a glance.
- **Quick Actions & Navigation**: Instantly navigate to courses, view course study materials, or open attendance details.
- **Dynamic Empty States**: Contextual prompts guiding users to add courses, assignments, or notes when starting fresh.

### 📚 2. Course Management
- **Course Directory**: Organizes course codes, titles, professor/instructor details, class schedules, and room numbers.
- **Interactive Jetpack Compose BottomSheets**: Effortlessly create, edit, or delete course information with modern M3 components.
- **Course Linking**: Directly connects courses with relevant study notes and assignment completion statistics.

### 📝 3. Assignment Tracking
- **Task Organization**: Track assignments by subject, due date, status (*Pending*, *In Progress*, *Completed*), and priority (*High*, *Medium*, *Low*).
- **Completion Metrics**: Live progress bars per course showing completed vs. total assignments.
- **Seamless Modal Editor**: Add and update assignment details using custom Jetpack Compose UI sheets.

### 📓 4. Smart Notes & Study Materials
- **Categorized Notes**: Create and organize study notes linked to specific subjects/courses.
- **Full Text Search & Filtering**: Quickly locate notes using titles or course names.
- **Note Detail View & BottomSheet**: View full notes, edit contents, or clean up outdated entries smoothly.

### 📅 5. Interactive Calendar & Attendance Logger
- **Custom Compose Calendar View**: Full monthly calendar built with Jetpack Compose (`CalendarMonthView`, `CalendarScreen`).
- **Gesture Attendance Logger**: Tap/double-tap/triple-tap calendar dates to toggle between **Present (🟢)** and **Absent (🔴)** attendance records.
- **Schedule & Event Manager**: Schedule academic events (Exams, Quizzes, Projects, Reminders) and view daily schedules.

### 👤 6. User Profile & Onboarding
- **Onboarding Carousel**: Multi-screen onboarding introducing features to first-time users (`ViewPager2` + custom adapters).
- **Local Authentication**: User registration and login persisted securely via Room database (`User` entity) and `SharedPreferences`.
- **Profile Management**: Displays student ID, major, semester, email, and academic stats.

---

## 🛠️ Technology Stack & Architecture

| Layer | Technology / Library |
| :--- | :--- |
| **Language** | [Kotlin 2.1.0](https://kotlinlang.org/) |
| **UI Framework** | Jetpack Compose (Material 3 BOM `2026.08.00`), ViewBinding, ViewPager2, Material Components (`1.14.0`) |
| **Architecture** | MVVM-inspired component architecture with Lifecycle-aware Coroutines |
| **Local Database** | [Room DB 2.8.5](https://developer.android.com/training/data-storage/room) with KSP (`Google DevTools KSP`) |
| **Asynchronous Processing**| Kotlin Coroutines & `lifecycleScope` |
| **Asset & Theme Engine** | Jetpack Compose M3 Theme + Material Design 3 XML Themes |
| **Gradle / Build Tool** | Gradle KTS with Version Catalogs (`libs.versions.toml`), AGP 9.2.1 |

---

## 🗄️ Database Architecture

The local SQLite database (`student_organizer_database`) is managed via Room ORM and contains five main entities:

```
+------------------+         +------------------+         +------------------+
|       User       |         |      Course      |         |    Assignment    |
+------------------+         +------------------+         +------------------+
| id (PK)          |         | id (PK)          |         | id (PK)          |
| name             |         | code             |         | title            |
| email            |         | name             |         | subject          |
| password         |         | instructor       |         | dueDate          |
| studentId        |         | room             |         | priority         |
| major, semester  |         | scheduleTime     |         | status           |
+------------------+         +------------------+         +------------------+

                             +------------------+         +------------------+
                             |      Notes       |         | AttendanceRecord |
                             +------------------+         +------------------+
                             | id (PK)          |         | date (PK)        |
                             | title            |         | isPresent        |
                             | subject          |         +------------------+
                             | content          |
                             | date             |
                             +------------------+
```

---

## 📂 Project Structure

```
StudentOrganizer/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/studentorganizer/
│   │   │   │   ├── activities/      # Splash, Onboarding, Login, Register, MainActivity
│   │   │   │   ├── adapter/         # ViewPager & RecyclerView Adapters
│   │   │   │   ├── compose/         # Jetpack Compose UI (Calendar, Course, Note, Assignment)
│   │   │   │   ├── database/        # Room Database, DAOs, and Entities
│   │   │   │   ├── fragments/       # Home, Course, Notes, Calendar, Assignment, Profile
│   │   │   │   ├── model/           # Data Models
│   │   │   │   └── ui/theme/        # Compose M3 Color, Typography, & Theme definitions
│   │   │   └── res/                 # XML Layouts, Drawables, Menus, Values
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml           # Centralized dependency versions
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Jellyfish or newer (2024.1+)
- **JDK**: Version 11 or higher
- **Android SDK**: Minimum API Level 24 (Android 7.0) / Target API Level 35

### Building & Running
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Kartikk-git/Student_Organizer.git
   cd Student_Organizer
   ```
2. **Open in Android Studio**:
   - Launch Android Studio and choose **Open an Existing Project**.
   - Select the `Student_Organizer` root directory.
3. **Gradle Sync**:
   - Allow Android Studio to sync Gradle dependencies (defined in `gradle/libs.versions.toml`).
4. **Run Application**:
   - Connect an Android device or launch an Emulator (API 24+).
   - Press `Shift + F10` or click **Run > Run 'app'**.

---

## 📸 Screenshots

| Onboarding | Dashboard | Courses |
| :-: | :-: | :-: |
| *Onboarding Screen* | *Home Dashboard View* | *Course List & Progress* |

| Assignments | Calendar & Attendance | Smart Notes |
| :-: | :-: | :-: |
| *Task & Priority Manager* | *Interactive Compose Calendar* | *Notes Directory* |

---

## 🔮 Future Roadmap & Scope

To evolve **Student Organizer** into a comprehensive, enterprise-grade campus ecosystem, the following features are planned for upcoming releases:

### 👑 1. Institutional Admin Portal & Control Hub
* **Role-Based Access Control (RBAC)**: Distinct permissions for **Administrators/Faculty** and **Students**.
* **Centralized Content Control**: Admins can publish course schedules, assign coursework, track class-wide attendance, and announce institute events.
* **Student Data Management**: Upload student records, enrollments, and academic transcripts via CSV/Excel or REST APIs.
* **Interactive Course Progress Analytics**: Students can view official course completion progress published directly by professors.

### 💳 2. Digital Document & Pass Generator
* **Digital Student ID Card**: Generate secure, scannable digital student IDs with embedded QR codes.
* **Campus Bus & Transit Pass**: Integrated digital bus/transport pass generation and verification.
* **Fee Statements & Receipts**: View, download, and track tuition fee structures, payment status, and official receipts.

### 🔔 3. Real-Time Cloud Sync & Notifications
* **Push Notifications**: Firebase Cloud Messaging (FCM) integration for urgent event updates, assignment due dates, and attendance warnings.
* **Cross-Device Backup**: Secure cloud database synchronization allowing students to restore data across devices.

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the app or report a bug:
1. Fork the project repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
