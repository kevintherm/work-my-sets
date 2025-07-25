package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.models.Schedule
import com.example.workmysets.data.models.ScheduleWithWorkouts
import com.example.workmysets.data.repositories.ExerciseRepository
import com.example.workmysets.data.repositories.ScheduleRepository
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScheduleRepository
    val scheduleWithWorkouts: LiveData<ScheduleWithWorkouts>

    init {
        val dao = AppDatabase.getDatabase(application).scheduleDao()
        repository = ScheduleRepository(dao)
        scheduleWithWorkouts = repository.scheduleWithWorkouts
    }

    suspend fun getSchedule(): Schedule? {
        return repository.getSchedule()
    }

    suspend fun createSchedule(schedule: Schedule): Schedule {
        repository.insert(schedule)
        return schedule.copy()
    }


}