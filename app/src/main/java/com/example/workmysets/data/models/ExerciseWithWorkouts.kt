package com.example.workmysets.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExerciseWithWorkouts(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "workoutId",
        associateBy = Junction(WorkoutExerciseCrossRef::class)
    )
    val workouts: List<Workout>
)