package com.example.workmysets.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class ScheduleWithWorkouts(
    @Embedded val schedule: Schedule,

    @Relation(
        parentColumn = "scheduleId",
        entityColumn = "scheduleOwnerId"
    )
    val workouts: List<Workout>
)