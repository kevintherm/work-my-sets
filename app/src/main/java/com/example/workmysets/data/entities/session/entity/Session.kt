package com.example.workmysets.data.entities.session.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.objects.TimestampPair
import java.time.Duration
import java.time.Instant

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["workoutId"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Session(
    val workoutId: Long,
    val exerciseId: Long
){
    @PrimaryKey(autoGenerate = true) var sessionId: Long = 0

    var exerciseOrder: Int = 0       // 1st, 2nd, etc. in the workout

    var repsPerSet: List<Int> = listOf()     // like [10, 8, 6]
    var weightsPerSet: List<Float> = listOf() // like [50f, 55f, 60f]
    var setsTimestamp: List<TimestampPair> = listOf() // [[Timestamp, Timestamp]]
    var restsPerSet: List<Int> = listOf()

    var isCompleted: Boolean = false

    var notes: String? = null       // optional notes
    var startsAt: String = ""
    var endsAt: String = ""
}
