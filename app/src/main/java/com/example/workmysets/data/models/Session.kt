package com.example.workmysets.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @ColumnInfo(name = "timestamp") val timestamp: String
){
    @PrimaryKey(autoGenerate = true) var sessionId: Long = 0
}
