package com.example.workmysets.data.entities.session.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.workmysets.data.entities.session.relation.SessionWorkoutCrossRef
import com.example.workmysets.data.entities.workout.entity.Workout

data class SessionWithWorkouts(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "workoutId",
        associateBy = Junction(SessionWorkoutCrossRef::class)
    )
    val workouts: List<Workout>
)