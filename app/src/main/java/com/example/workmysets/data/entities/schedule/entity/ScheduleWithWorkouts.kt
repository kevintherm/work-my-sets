package com.example.workmysets.data.entities.schedule.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.workmysets.data.entities.workout.entity.Workout

data class ScheduleWithWorkouts(
    @Embedded val schedule: Schedule,

    @Relation(
        parentColumn = "scheduleId",
        entityColumn = "scheduleOwnerId"
    )
    val workouts: List<Workout>
)