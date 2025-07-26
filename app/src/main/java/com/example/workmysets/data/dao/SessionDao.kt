package com.example.workmysets.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.entity.SessionExerciseLog
import com.example.workmysets.data.entities.session.entity.SessionWithLogs
import com.example.workmysets.data.entities.session.entity.SessionWithWorkouts

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(log: SessionExerciseLog): Long

    @Transaction
    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    fun getSessionWithLogs(sessionId: Long): LiveData<SessionWithLogs>

    @Transaction
    @Query("SELECT * FROM session_exercises_logs WHERE sessionId = :sessionId")
    fun getExerciseLogsForSession(sessionId: Long): LiveData<List<SessionExerciseLog>>

    @Query("SELECT * FROM sessions")
    fun getSesions(): LiveData<List<Session>>
}