# Smart University Attendance App

An Android app that allows students to mark and view their attendance. Students authenticate via Firebase, enter a teacher-issued auth code, and the app verifies their GPS proximity to the classroom before recording attendance.

## Features

- Student registration with year, branch, and section selection
- PIN-protected login and registration flow
- GPS proximity check — attendance is only recorded when the student is within range of the teacher's location
- Auth code validation per subject per class session
- View overall attendance percentage and per-subject breakdown
- Password reset via email
- Firebase Realtime Database backend shared with the companion SmartAttendance for Teacher app

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Platform | Android (minSdk 14, targetSdk 27) |
| Build system | Gradle 4.4 |
| Backend | Firebase Realtime Database |
| Authentication | Firebase Authentication (email/password) |
| Location | Android LocationManager + Google Play Services Fused Location |
| UI | Android Support Library (AppCompat, RecyclerView, CardView, Design) |

## Prerequisites

- Android Studio (any version that supports Gradle 4.4 / AGP 3.1.2)
- Android SDK 27
- A Firebase project with **Realtime Database** and **Authentication (email/password)** enabled
- The companion **SmartAttendance for Teacher** app configured against the same Firebase project

## Getting Started

### 1. Clone the repository

```bash
git clone <repo-url>
cd SmartAttendanceforStudent
```

### 2. Add `google-services.json`

This file is not committed to the repository because it contains your Firebase API key.

1. Go to the [Firebase Console](https://console.firebase.google.com/) and open your project.
2. Navigate to **Project Settings → Your apps → Android app**.
3. Download `google-services.json` and place it at `app/google-services.json`.

A template showing the expected structure is at `app/google-services.json.example`.

### 3. Configure Firebase

In the Firebase Console, enable:
- **Authentication → Sign-in method → Email/Password**
- **Realtime Database** — import or match the schema used by the teacher app (`emails`, `addedSubjects`, `addedStudents`, `attendance`, `AuthCodes`, `noOfClasses`, `LocationCoordinates`, `pinForRegistration`)

### 4. Build and run

Open the project in Android Studio and run on a device or emulator (API 14+). Or build from the command line:

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project Structure

```
app/
  src/main/
    java/.../smartattendanceforstudent/
      Login.java                  # Entry point — email/password + PIN login
      Register.java               # New student registration
      ForgotPassword.java         # Password reset via Firebase email
      Subjects.java               # Lists subjects for the student's year/branch/section
      AttendanceInStudent.java    # Auth code entry + GPS validation + attendance recording
      CheckAttendanceInStudent.java  # Overall attendance summary
      AdapterClassForSubjects.java   # ListView adapter for subjects
      AdapterClassForStudents.java   # ListView adapter for students
      CardClass.java              # Model for list cards
      Branch/Year/Sections/Students.java  # Navigation drill-down screens
    res/
      layout/     # XML layouts for each Activity
      values/     # strings, colors, dimens, styles
  google-services.json.example    # Template — copy and rename to google-services.json
build.gradle          # Root build file (AGP + Google Services plugin)
app/build.gradle      # App-level dependencies and SDK config
settings.gradle
```

## Security Notes

- `app/google-services.json` is excluded from version control via `.gitignore`. Each developer must obtain it directly from the Firebase Console.
- If this file was previously committed to git history, rotate your Firebase API key in the Firebase Console immediately (**Project Settings → Your apps → Regenerate**) and revoke the old OAuth credentials in Google Cloud Console.
- Registration and login are gated by a PIN set by the teacher in the companion app, limiting self-sign-up.

## License

No license file is present in this repository.
