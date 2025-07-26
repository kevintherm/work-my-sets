package com.example.workmysets.data.entities.exercise.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.entities.workout.relation.WorkoutExerciseCrossRef

data class ExerciseWithWorkouts(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "workoutId",
        associateBy = Junction(WorkoutExerciseCrossRef::class)
    )
    val workouts: List<Workout>
)