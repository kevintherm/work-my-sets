package com.example.workmysets.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    val name: String,
    val description: String? = null,
){
    @PrimaryKey(autoGenerate = true) var workoutId: Long = 0
}
