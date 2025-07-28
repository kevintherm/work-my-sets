package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.SessionDao
import com.example.workmysets.data.entities.session.entity.Session

class SessionRepository(private val dao: SessionDao) {

    fun getSessionByWorkoutId(workoutId: Long): LiveData<List<Session>> {
        return dao.getSessionByWorkoutId(workoutId)
    }

    suspend fun insert(session: Session) {
        return dao.insert(session)
    }

}