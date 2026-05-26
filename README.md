# StudyStreak – Habit Tracker App

StudyStreak is a mobile habit tracker app for students.  
The app helps users organize their study routine by combining a to-do list, a focus timer, and progress statistics.

## Project Idea

Many students struggle with staying consistent while studying. StudyStreak supports users by allowing them to plan daily study tasks, track focus sessions, and visualize their learning progress over time.

The goal is to make studying more structured and motivating.

## Main Features

- Create study tasks
- Mark tasks as completed
- Delete tasks with swipe gestures
- Start and stop a focus timer
- Save completed study sessions
- Track daily and weekly study time
- Show learning streaks
- Display progress statistics
- Clean and modern user interface

## Covered Project Requirements

The app covers at least two required topics from the project specification:

### 1. Data Centricity

StudyStreak uses a local database to store user data.

Stored data includes:

- Study tasks
- Completed tasks
- Study sessions
- Timer duration
- Daily progress
- Streak information

The database is implemented locally on the device using Room.

### 2. Special Gestures

The app uses gestures to improve usability.

Examples:

- Swipe to delete a task
- Swipe or tap to mark tasks as completed

### 3. Outstanding Look and Feel

The app provides a modern and clean design using Jetpack Compose.

The user interface includes:

- Dashboard cards
- Progress bars
- Timer screen
- Statistics screen
- Simple and clear navigation

## Technology Stack

- Kotlin
- Android
- Jetpack Compose
- Room Database
- ViewModel
- StateFlow
- MVVM Architecture
- Gradle

## App Architecture

The app follows the MVVM architecture pattern.

```text
UI Screens
↓
ViewModel
↓
Repository
↓
Room DAO
↓
Room Database
