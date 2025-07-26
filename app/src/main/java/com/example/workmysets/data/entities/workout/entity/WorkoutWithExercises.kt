package com.example.workmysets.data.entities.workout.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.workmysets.data.entities.workout.relation.WorkoutExerciseCrossRef
import com.example.workmysets.data.entities.exercise.entity.Exercise

data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "exerciseId",
        associateBy = Junction(WorkoutExerciseCrossRef::class)
    )
    val exercises: List<Exercise>
)