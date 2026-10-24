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
   git clone https://github.com/your-username/StudentOrganizer.git
   cd StudentOrganizer
   ```
2. **Open in Android Studio**:
   - Launch Android Studio and choose **Open an Existing Project**.
   - Select the `StudentOrganizer` root directory.
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
