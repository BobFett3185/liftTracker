# LiftTracker

LiftTracker is a simple offline-first Android app for tracking gym workouts without the friction of spreadsheets, notes apps, or complicated fitness platforms.

The app is built for lifters who want to record sets quickly while they are actually training. The goal is to keep the experience clean, calm, and focused: open the app, choose the workout day, tap the exercise, enter reps and weight, and keep moving.

## What The App Solves

Many gym trackers become too busy, and spreadsheets are awkward on a phone during a workout. LiftTracker keeps the workout flow minimal by separating planning from logging.

Users can define their training split once, then reuse it whenever they start a workout. This removes repeated typing and keeps the active workout screen clean.

## Core Workflow

1. **Build a split**
   Create split days such as Push, Pull, Legs, Upper, or Lower. Add the exercises normally performed on each day.

2. **Start a workout**
   Choose one of the split days. The workout is created with those exercises already filled in.

3. **Log sets**
   Tap an exercise to open a focused entry screen. Add reps and weight without the keyboard covering a crowded workout list.

4. **Adjust on the spot**
   Rename an exercise during the workout if plans change, such as swapping Bench Press for Dumbbell Press.

5. **Review history**
   The home screen shows workouts in a monthly calendar format. Workout days are marked, and tapping a day shows the sessions logged for that date.

6. **Track progress**
   The progress screen shows simple weight-over-time data for selected exercises.

## Design Goals

- Fast to use during a real workout
- Clean screens with minimal clutter
- Split templates to avoid repeated typing
- Dedicated exercise entry screen for easier keyboard use
- Local-only storage with no login or backend
- Calm cream and brown visual theme
- Simple MVP over unnecessary features

## Tech Stack

- Kotlin
- Jetpack Compose
- Room local database
- MVVM-style ViewModels and repository layer
- Navigation Compose

## Data Stays On The Phone

LiftTracker is offline-first. Workouts, split days, exercises, and sets are stored locally on the device using Room. There is no account system, cloud sync, or backend service.
