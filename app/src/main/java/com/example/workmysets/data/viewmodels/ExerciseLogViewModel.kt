package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.repositories.SessionRepository

class ExerciseLogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SessionRepository



    init {
        val dao = AppDatabase.getDatabase(application).SessionDao()
        repository = SessionRepository(dao)
    }


}