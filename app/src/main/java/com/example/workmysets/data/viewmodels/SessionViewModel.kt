package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.entity.SessionWithLogs
import com.example.workmysets.data.repositories.SessionRepository

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SessionRepository
    val allSessions: LiveData<List<Session>>

    init {
        val dao = AppDatabase.getDatabase(application).SessionDao()
        repository = SessionRepository(dao)
        allSessions = repository.allSessions
    }


}