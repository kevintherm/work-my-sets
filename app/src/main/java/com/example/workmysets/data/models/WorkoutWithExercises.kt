package com.example.workmysets.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "exerciseId",
        associateBy = Junction(WorkoutExerciseCrossRef::class)
    )
    val exercises: List<Exercise>
)