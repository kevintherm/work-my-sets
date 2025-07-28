package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.repositories.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository
    val allWorkouts: LiveData<List<WorkoutWithExercises>>

    private val _selectedWorkout = MutableLiveData<WorkoutWithExercises?>()
    val selectedWorkout: LiveData<WorkoutWithExercises?> = _selectedWorkout

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WorkoutRepository(db.workoutDao(), db.exerciseDao())
        allWorkouts = repository.allWorkouts
    }

    fun insertWorkoutWithExercises(workout: Workout, exercises: List<Exercise>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertWorkoutWithExercises(workout, exercises)
    }

    fun findById(id: Long): LiveData<WorkoutWithExercises>{
        return repository.findById(id)
    }

    fun update(workout: Workout) = viewModelScope.launch {
        repository.update(workout)
    }

    fun updateWorkoutWithExercises(workout: Workout, exercises: List<Exercise>) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateWorkoutWithExercises(workout, exercises)
    }

    fun deleteWorkout(workout: WorkoutWithExercises) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteWorkout(workout)
    }
}
