package com.example.workmysets.data.models

import androidx.room.Entity

@Entity(primaryKeys = ["sessionId", "workoutId"])
data class SessionWorkoutCrossRef(
    val sessionId: Long,
    val workoutId: Long
)