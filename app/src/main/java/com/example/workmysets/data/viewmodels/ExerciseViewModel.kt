package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.repositories.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExerciseRepository
    val allExercises: LiveData<List<Exercise>>
    val searchResult = MutableLiveData<List<Exercise>>()

    init {
        val dao = AppDatabase.getDatabase(application).exerciseDao()
        repository = ExerciseRepository(dao)
        allExercises = repository.allExercises
    }

    fun searchExercises(query: String) {
        viewModelScope.launch {
            repository.searchExercises(query).observeForever {
                searchResult.value = it
            }
        }
    }

    fun findById(exerciseId: Long): LiveData<Exercise> {
        return repository.findById(exerciseId)
    }

}