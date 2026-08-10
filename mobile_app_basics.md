# Mobile App Basics — Android fundamentals and how this app uses them

This document covers core Android concepts and maps them to the liftTracker project so you can quickly understand the stack and where things live.

## 1) High-level stack
- Language: Kotlin
- Build system: Gradle (`build.gradle`, `app/build.gradle`, `gradle/wrapper`)
- UI: Jetpack Compose (project `ui/screens/*` files indicate Compose screens)
- Persistence: Room (entities/DAOs under `app/src/main/java/.../data/`)
- State & lifecycle: `ViewModel` classes (e.g. `NewWorkoutViewModel.kt`)
- Packaging & tooling: Android SDK + Gradle wrapper (`gradlew`, `gradlew.bat`)

## 2) Core Android concepts (short)
- Activity: an entrypoint representing a single screen container. This app's `MainActivity.kt` hosts Compose and the `NavHost`/navigation.
- Composable / UI: UI declared in Kotlin functions using Jetpack Compose. Files under `ui/screens/` implement screens.
- ViewModel: holds UI state and business logic across configuration changes.
- Repository + DAO: repository abstracts data access; DAOs (Room) run SQL interactions and return entities.
- Room Entities: data classes annotated for Room that represent DB tables (e.g. `ExerciseEntryEntity.kt`, `SetEntryEntity.kt`, `WorkoutWithExercises.kt`).
- Navigation: Compose Navigation (or manual nav) moves between composable screens.

## 3) How data flows in this app
1. UI (Compose screen) collects user input (e.g. `NewWorkoutScreen.kt`, `ExerciseEntryScreen.kt`).
2. UI calls into a `ViewModel` (e.g. `NewWorkoutViewModel.kt`) to handle user actions.
3. `ViewModel` uses the `LiftTrackerRepository` (see `LiftTrackerRepository.kt`) to persist or query data.
4. Repository uses `LiftTrackerDao` (see `LiftTrackerDao.kt`) which is a Room DAO to read/write the database.
5. Room stores data in an SQLite DB; queries return entity objects and, through mapping, domain models used by the UI.

This pattern (UI → ViewModel → Repository → DAO → Room) keeps concerns separated and testable.

## 4) Files in this project and their roles
- `MainActivity.kt`: app entrypoint, sets up Compose and likely the navigation host.
- `ui/screens/*.kt` (e.g. `CalendarScreen.kt`, `NewWorkoutScreen.kt`, `ExerciseEntryScreen.kt`): Composable screens and UI logic.
- `NewWorkoutViewModel.kt`: ViewModel for the new-workout flow (holds state, validation, and calls repository).
- `data/`:
  - `ExerciseEntryEntity.kt`, `SetEntryEntity.kt`, `WorkoutWithExercises.kt`: Room entities and relations.
  - `LiftTrackerDao.kt`: Room DAO with SQL queries.
  - `LiftTrackerRepository.kt`: repository bridging ViewModel and DAO.

## 5) Build & run basics
- Build: `./gradlew assembleDebug` or use Android Studio's Run.
- Clean: `./gradlew clean`.
- Stop Gradle daemons if needed: `./gradlew --stop`.

## 6) Common dev tasks & tips
- Keep `.gradle/`, build outputs, and local machine files out of VCS—use `.gitignore` (already added).
- Use `ViewModel` for long-lived state so configuration changes (rotation) don't lose input.
- Run database migrations carefully when changing Room entities; Room will require a migration path if schemas change.
- If the app uses Kotlin coroutines or Flow (likely in repository/DAO), prefer performing DB I/O on `Dispatchers.IO` and exposing cold flows to UI.

## 7) Quick debugging pointers
- Logcat for runtime errors and stack traces.
- Use Compose Preview and/or run on emulator / physical device for UI testing.
- Use `adb logcat` for device logs and `./gradlew connectedAndroidTest` for instrumentation tests.

## 8) Where to look next in this repo
- UI: `app/src/main/java/com/example/lifttracker/ui/screens/` — read the composable screens.
- Data: `app/src/main/java/com/example/lifttracker/data/` — DAO, entities, repository.
- App shell: `app/src/main/java/com/example/lifttracker/MainActivity.kt` — wiring and navigation.

If you want, I can:
- open and annotate the key files with comments mapping concepts to lines, or
- generate a simple diagram of the data flow (UI → VM → Repo → DAO → DB).

---
Created to help onboard contributors and remind maintainers of the architecture and roles of files.
