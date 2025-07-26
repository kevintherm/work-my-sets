package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.SessionDao
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.entity.SessionExerciseLog
import com.example.workmysets.data.entities.session.entity.SessionWithLogs

class SessionRepository(private val dao: SessionDao) {
    val allSessions = dao.getSesions()

    suspend fun insert(session: Session): Long {
        return dao.insert(session)
    }

    suspend fun update(session: Session) {
        return dao.update(session)
    }

    suspend fun insertExerciseLog(sessionExerciseLog: SessionExerciseLog): Long {
        return dao.insertExerciseLog(sessionExerciseLog)
    }

    fun getSessionWithLogs(sessionId: Long): LiveData<SessionWithLogs> {
        return dao.getSessionWithLogs(sessionId)
    }
}