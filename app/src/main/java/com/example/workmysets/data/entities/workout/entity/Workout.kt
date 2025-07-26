package com.example.workmysets.data.entities.workout.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.workmysets.data.entities.schedule.entity.Schedule

@Entity(
    tableName = "workouts",
    foreignKeys = [ForeignKey(
        entity = Schedule::class,
        parentColumns = ["scheduleId"],
        childColumns = ["scheduleOwnerId"],
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index(value = ["scheduleOwnerId"])]
)
data class Workout(
    val name: String,
    val description: String? = null,
    var dayOfWeek: Int = -1,
    var scheduleOwnerId: Long = 1
){
    @PrimaryKey(autoGenerate = true) var workoutId: Long = 0
    var mastery: Float = 0f
}
