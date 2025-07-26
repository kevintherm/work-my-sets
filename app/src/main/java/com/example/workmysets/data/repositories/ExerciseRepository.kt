package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.ExerciseDao
import com.example.workmysets.data.entities.exercise.entity.Exercise

class ExerciseRepository(private val dao: ExerciseDao) {
    val allExercises: LiveData<List<Exercise>> = dao.getAll()

    suspend fun insert(exercise: Exercise): Long {
        return dao.insert(exercise)
    }

    fun searchExercises(search: String): LiveData<List<Exercise>> {
        return dao.searchExercises(search)
    }
}