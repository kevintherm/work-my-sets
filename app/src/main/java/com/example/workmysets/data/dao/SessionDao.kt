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
import com.example.workmysets.data.entities.session.entity.SessionWithWorkouts

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: Session)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(session: Session)

    @Transaction
    @Query("SELECT * FROM sessions WHERE workoutId = :workoutId")
    fun getSessionByWorkoutId(workoutId: Long): LiveData<List<Session>>
}