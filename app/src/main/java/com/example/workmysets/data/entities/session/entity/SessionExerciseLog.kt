package com.example.workmysets.data.entities.session.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_exercises_logs")
data class SessionExerciseLog(
    val sessionId: Long,
    val exerciseId: Long,
    val workoutId: Long,  // idk why this here

    val exerciseOrder: Int,         // 1st, 2nd, etc. in the workout
    val startTimestamp: String,     // when this exercise started
    val endTimestamp: String,       // when this exercise ended

    val repsPerSet: List<Int>,      // like [10, 8, 6]
    val weightsPerSet: List<Float>, // like [50f, 55f, 60f]
    val restDurations: List<Long>,  // milliseconds between sets [60000, 90000]

    val notes: String? = null       // optional notes
) {
    @PrimaryKey(autoGenerate = true)
    var logId: Long = 0
}