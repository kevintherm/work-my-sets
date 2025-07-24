package com.example.workmysets.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WorkoutWithSessions(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "sessionId",
        associateBy = Junction(SessionWorkoutCrossRef::class)
    )
    val sessions: List<Session>
)