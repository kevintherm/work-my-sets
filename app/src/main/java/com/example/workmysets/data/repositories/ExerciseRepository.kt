package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.ExerciseDao
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.session.entity.Session

class ExerciseRepository(private val dao: ExerciseDao) {
    val allExercises: LiveData<List<Exercise>> = dao.getAll()

    suspend fun update(exercise: Exercise) {
        return dao.update(exercise)
    }

    suspend fun insert(exercise: Exercise): Long {
        return dao.insert(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        return dao.delete(exercise)
    }

    fun searchExercises(search: String): LiveData<List<Exercise>> {
        return dao.searchExercises(search)
    }

    fun findById(exerciseId: Long): LiveData<Exercise> {
        return dao.findById(exerciseId)
    }
}