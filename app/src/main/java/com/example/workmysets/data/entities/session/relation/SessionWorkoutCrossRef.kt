package com.example.workmysets.data.entities.session.relation

import androidx.room.Entity

@Entity(primaryKeys = ["sessionId", "workoutId"])
data class SessionWorkoutCrossRef(
    val sessionId: Long,
    val workoutId: Long
)