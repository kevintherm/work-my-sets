package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.SessionDao
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.entity.SessionWithExercise

class SessionRepository(private val dao: SessionDao) {

    val allSessions = dao.getAllSessionsWithExercise()

    fun getSessionByWorkoutId(workoutId: Long): LiveData<List<SessionWithExercise>> {
        return dao.getSessionByWorkoutId(workoutId)
    }

    suspend fun insert(session: Session) {
        return dao.insert(session)
    }

}