package com.example.workmysets.data.entities.workout.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.relation.SessionWorkoutCrossRef

data class WorkoutWithSessions(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "sessionId",
        associateBy = Junction(SessionWorkoutCrossRef::class)
    )
    val sessions: List<Session>
)