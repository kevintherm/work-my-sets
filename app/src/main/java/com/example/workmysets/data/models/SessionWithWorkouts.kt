package com.example.workmysets.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SessionWithWorkouts(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "workoutId",
        associateBy = Junction(SessionWorkoutCrossRef::class)
    )
    val workouts: List<Workout>
)