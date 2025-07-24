package com.example.workmysets.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    val name: String,
    val targetMuscle: String,
    val calorieBurned: Double
) {
    @PrimaryKey(autoGenerate = true)
    var exerciseId: Long = 0

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        return other is Exercise && other.exerciseId == this.exerciseId
    }

    override fun hashCode(): Int {
        return exerciseId.hashCode()
    }
}