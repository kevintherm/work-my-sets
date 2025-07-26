package com.example.workmysets.data.entities.workout.relation

import androidx.room.Entity

@Entity(
    tableName = "workout_exercise_cross_ref",
    primaryKeys = ["workoutId", "exerciseId"],
)
data class WorkoutExerciseCrossRef(
    val workoutId: Long,
    val exerciseId: Long
)
