package com.example.workmysets.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.workmysets.data.models.Schedule
import com.example.workmysets.data.models.ScheduleWithWorkouts

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule): Long

    @Delete
    suspend fun delete(schedule: Schedule): Int

    @Query("SELECT * FROM schedules WHERE scheduleId = 1 LIMIT 1")
    suspend fun getSchedule(): Schedule?

    @Transaction
    @Query("SELECT * FROM schedules WHERE scheduleId = 1 LIMIT 1")
    fun getScheduleWithWorkouts(): LiveData<ScheduleWithWorkouts>
}