package com.example.workmysets.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    val name: String
){
    @PrimaryKey
    var scheduleId: Long = 1
}
