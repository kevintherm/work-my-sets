package com.example.workmysets.data.samples

import com.example.workmysets.data.entities.exercise.entity.Exercise

object SampleData {
    val exercises = listOf(
        Exercise("Push Up", "Chest", 5.0),
        Exercise("Squat", "Legs", 6.0),
        Exercise("Plank", "Core", 4.0),
        Exercise("Pull Up", "Back", 7.5),
        Exercise("Bicep Curl", "Arms", 3.5),
        Exercise("Lunges", "Legs", 5.5),
        Exercise("Burpees", "Full Body", 8.0)
    )
}