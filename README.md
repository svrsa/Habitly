# Habitly

Habitly is a study habit tracker app for students.

The app combines task management, a focus timer, study planning, reminders, and progress statistics. The goal is to help students turn study goals into concrete focus sessions and make their learning progress visible over time.

## Project Context

This project was created for the course **UE App Development SS 2026**.

Habitly focuses on three required project topics:

- **Data centricity**, because study tasks, focus sessions, plans, and evidence entries are stored locally.
- **Special gestures**, because study tasks support swipe actions.
- **Outstanding look and feel**, because the app uses a clean Material 3 interface with cards, charts, progress indicators, and a study heatmap.

## Main Features

- Create, complete, delete, filter, and prioritize study tasks.
- Use swipe gestures to complete or delete tasks.
- Start a focus timer with configurable durations.
- Save completed and partial focus sessions.
- Plan study tasks as focus blocks for specific days.
- Start a planned focus block directly from the study planner.
- Track daily progress, total focus time, completed sessions, and learning streaks.
- View weekly progress and an 8-week study heatmap.
- View and delete recent focus sessions.
- Configure default focus duration and daily study goal.
- Enable daily study reminders with a custom reminder time.
- Capture and review study evidence in a journal.

## Technology Stack

- Kotlin
- Android
- Jetpack Compose
- Material 3
- Room Database
- DataStore Preferences
- ViewModel
- StateFlow and Flow
- MVVM architecture
- Gradle
- CameraX
- Android notifications and AlarmManager

## Architecture

Habitly follows an MVVM-based architecture.

```text
Compose Screens
↓
ViewModel
↓
Repository
↓
DAO
↓
Room Database / DataStore
```

The Compose screens display the UI and forward user actions to ViewModels. ViewModels hold the screen state and call repositories. Repositories hide the concrete data source from the UI layer. Room DAOs define the database operations, while entities define the database tables.

Most app data is exposed as `Flow` and collected in ViewModels as `StateFlow`, so the UI updates automatically when stored data changes.

## Data Model

Habitly stores the main study data locally with Room:

- **Study tasks**: title, completion state, priority, creation time.
- **Study sessions**: saved focus duration, completion time, optional planned block reference.
- **Study plans**: selected task, planned date, block duration, planned blocks, completed blocks.
- **Study evidence**: captured proof or journal entries connected to focus sessions.

Settings are stored with DataStore:

- Default focus duration.
- Daily study goal.
- Daily reminder enabled state.
- Reminder time.

## Core Screens

### Dashboard

The dashboard is the main entry point. It shows today's focus time, daily goal progress, learning streak, task overview, saved focus blocks, and today's study plan.

### Tasks

The tasks screen lets users create study tasks, choose priorities, filter tasks, mark tasks as completed, and delete tasks. Swipe gestures are used for quick task actions.

### Study Planner

The planner turns open tasks into concrete focus blocks for a selected day. A planned block can be started directly in the timer, and completed timer sessions update the plan progress.

### Timer

The focus timer supports free focus sessions and planned focus sessions. Completed sessions are saved locally and used for statistics. Partial sessions can also be saved when meaningful progress was made.

### Statistics

The statistics screen visualizes learning progress with total focus time, session count, task progress, learning streaks, a weekly chart, a study heatmap, and recent sessions.

### Settings

The settings screen lets users configure default focus duration, daily study goal, and daily reminder time. Reminder settings are persisted and used to schedule notifications.

### Evidence Journal

The evidence flow allows users to capture and review study evidence connected to their learning sessions.

## Project Requirements

### Data Centricity

Habitly uses Room as a local database for study tasks, focus sessions, study plans, and evidence data. The app also uses DataStore for persistent user settings.

### Special Gestures

The tasks screen supports swipe actions:

- Swipe one direction to complete or reopen a task.
- Swipe the other direction to delete a task.

### Outstanding Look and Feel

Habitly uses Jetpack Compose and Material 3 for a modern interface. The UI includes dashboard cards, rounded surfaces, progress indicators, charts, an interactive heatmap, and clean bottom navigation.

## Team and Contributions

### Stefan

- Core application architecture and navigation.
- Task management and swipe interactions.
- Focus timer and session persistence.
- Statistics, dashboard, and task history.
- UI design and visual refinement.
- Testing and bug fixing.

### Phillip

- Study planner and planned focus workflow.
- Evidence journal and camera integration.
- Persistent settings and study reminders.
- Learning streak and study heatmap.
- Testing and feature refinement.

The team collaborated on planning, testing, design decisions, debugging, documentation, and the final presentation.

## Libraries and External Resources

Habitly uses the following Android and Jetpack libraries:

- Jetpack Compose and Material 3 for the user interface.
- Room for local database persistence.
- DataStore Preferences for application settings.
- CameraX for study evidence capture.
- Android Lifecycle, ViewModel, Flow, and StateFlow for application state.
- AlarmManager and Android notifications for study reminders.
- JUnit and AndroidX testing libraries for automated tests.

Official Android and Jetpack documentation was used as a development reference.

## Use of Artificial Intelligence

AI-assisted tools were used during development for brainstorming, debugging, code suggestions, test support, UI refinement, and documentation.

All AI-assisted output was reviewed, adapted, and tested by the project members. The project members remain fully responsible for the submitted implementation.

## Build and Run

### Requirements

- Android Studio with its bundled Java runtime.
- Android SDK 36.
- An emulator or Android device running Android 15 (API level 35) or newer.

### Android Studio

1. Clone the repository.
2. Open the project in Android Studio.
3. Allow the Gradle synchronization to finish.
4. Select an emulator or connected Android device.
5. Run the `app` configuration.

### Command Line

Run the unit tests and create a debug APK with:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/`.

## Testing

The project includes unit tests for core application logic, including:

- Task filtering and grouping.
- Timer session save behavior.
- Planner state calculations.
- Learning streak calculation.
- Study heatmap calculation.
- Settings persistence.
- Reminder scheduling.
- Date and duration formatting.

Run all local unit tests with:

```bash
./gradlew testDebugUnitTest
```

## Current Scope

Habitly currently provides a solid study workflow:

```text
Create task
↓
Plan focus blocks
↓
Start timer
↓
Save focus session
↓
Review progress in statistics
```

Future improvements could include deeper planner automation, richer evidence review, improved onboarding, and more advanced statistics.
