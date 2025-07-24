package com.example.workmysets.data.models

import androidx.room.Entity

@Entity(
    tableName = "workout_exercise_cross_ref",
    primaryKeys = ["workoutId", "exerciseId"],
)
data class WorkoutExerciseCrossRef(
    val workoutId: Long,
    val exerciseId: Long
)
