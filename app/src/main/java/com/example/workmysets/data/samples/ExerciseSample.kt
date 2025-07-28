package com.example.workmysets.data.samples

import com.example.workmysets.data.entities.exercise.entity.Exercise

object SampleData {
    val exercises = listOf(
        // Chest
        Exercise("Push Up", "Chest", 0.29, "WDIpL0pjun0"),
        Exercise("Bench Press", "Chest", 0.35, "N4H4o8k9WbE"),
        Exercise("Incline Bench Press", "Chest", 0.36, "BjGLs6KGWUc"),
        Exercise("Chest Fly", "Chest", 0.27, "XNf6TBErGys"),
        Exercise("Dips", "Chest", 0.33, "WVeZDBhZwLA"),

        // Legs
        Exercise("Squat", "Legs", 0.45, "-bJIpOq-LWk"),
        Exercise("Lunges", "Legs", 0.42, ""),
        Exercise("Leg Press", "Legs", 0.40, ""),
        Exercise("Leg Extension", "Legs", 0.25, ""),
        Exercise("Leg Curl", "Legs", 0.25, ""),
        Exercise("Calf Raises", "Legs", 0.18, ""),
        Exercise("Jump Squat", "Legs", 0.50, ""),
        Exercise("Wall Sit", "Legs", 0.10, ""), // per second if held; adjust as needed

        // Core
        Exercise("Plank", "Core", 0.06, ""), // per second if held; reps can be time-based
        Exercise("Sit Up", "Core", 0.22, ""),
        Exercise("Crunches", "Core", 0.20, ""),
        Exercise("Bicycle Crunch", "Core", 0.24, ""),
        Exercise("Mountain Climbers", "Core", 0.28, ""),
        Exercise("Leg Raises", "Core", 0.23, ""),
        Exercise("Russian Twist", "Core", 0.21, ""),
        Exercise("Hanging Leg Raise", "Core", 0.30, ""),

        // Back
        Exercise("Pull Up", "Back", 0.40, ""),
        Exercise("Deadlift", "Back", 0.55, ""),
        Exercise("Bent Over Row", "Back", 0.38, ""),
        Exercise("Lat Pulldown", "Back", 0.33, ""),
        Exercise("T-Bar Row", "Back", 0.37, ""),
        Exercise("Superman", "Back", 0.20, ""),

        // Arms
        Exercise("Bicep Curl", "Arms", 0.20, ""),
        Exercise("Hammer Curl", "Arms", 0.21, ""),
        Exercise("Tricep Extension", "Arms", 0.22, ""),
        Exercise("Tricep Dips", "Arms", 0.26, ""),
        Exercise("Preacher Curl", "Arms", 0.21, ""),
        Exercise("Close-Grip Bench Press", "Arms", 0.31, ""),

        // Shoulders
        Exercise("Shoulder Press", "Shoulders", 0.30, ""),
        Exercise("Lateral Raise", "Shoulders", 0.22, ""),
        Exercise("Front Raise", "Shoulders", 0.21, ""),
        Exercise("Arnold Press", "Shoulders", 0.33, ""),
        Exercise("Upright Row", "Shoulders", 0.29, ""),

        // Full Body / Conditioning
        Exercise("Burpees", "Full Body", 0.50, ""),
        Exercise("Jump Rope", "Full Body", 0.45, ""),
        Exercise("Kettlebell Swing", "Full Body", 0.43, ""),
        Exercise("Clean and Press", "Full Body", 0.60, ""),
        Exercise("Thrusters", "Full Body", 0.55, ""),
        Exercise("Farmer’s Carry", "Full Body", 0.12, ""), // per step or second
        Exercise("Battle Ropes", "Full Body", 0.25, "") // per rep (or per second of motion)
    )
}
