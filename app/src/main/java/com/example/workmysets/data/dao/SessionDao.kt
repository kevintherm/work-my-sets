package com.example.workmysets.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.workmysets.data.models.Session
import com.example.workmysets.data.models.SessionWithWorkouts

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Delete
    suspend fun delete(session: Session)

    @Transaction
    @Query("SELECT * FROM sessions WHERE sessionId = :id")
    fun getSessionWithWorkouts(id: Long): LiveData<MutableList<SessionWithWorkouts>>
}