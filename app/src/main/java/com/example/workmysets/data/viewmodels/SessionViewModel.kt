package com.example.workmysets.data.viewmodels

import android.app.Application
import android.media.metrics.LogSessionId
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.repositories.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SessionRepository

    private var _allSessions = MutableLiveData<List<Session>>()

    init {
        val dao = AppDatabase.getDatabase(application).SessionDao()
        repository = SessionRepository(dao)
    }

    fun getSessionByWorkoutId(workoutId: Long): LiveData<List<Session>> {
        return repository.getSessionByWorkoutId(workoutId)
    }

    fun insert(session: Session) = viewModelScope.launch {
        repository.insert(session)
    }
}