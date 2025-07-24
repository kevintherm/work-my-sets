package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.ExerciseDao
import com.example.workmysets.data.dao.WorkoutDao
import com.example.workmysets.data.models.Exercise
import com.example.workmysets.data.models.Workout
import com.example.workmysets.data.models.WorkoutExerciseCrossRef
import com.example.workmysets.data.models.WorkoutWithExercises

class WorkoutRepository(private val workoutDao: WorkoutDao, private val exerciseDao: ExerciseDao) {
    val allWorkouts: LiveData<List<WorkoutWithExercises>> =
        workoutDao.getAllWorkoutsWithExercises()

    suspend fun insert(workout: Workout): Long {
        return workoutDao.insert(workout)
    }

    suspend fun insertWorkoutWithExercises(workout: Workout, exercises: List<Exercise>): Long {
        val workoutId = workoutDao.insert(workout)

        exercises.forEach {
            val exerciseId = exerciseDao.insert(it)
            val crossRef = WorkoutExerciseCrossRef(workoutId, exerciseId)
            workoutDao.insertCrossRef(crossRef)
        }

        return workoutId
    }

    fun findByName(name: String): LiveData<List<WorkoutWithExercises>> {
        return workoutDao.findByName(name)
    }

    fun findById(id: Long): WorkoutWithExercises {
        return workoutDao.getWorkoutWithExercises(id)
    }

    suspend fun updateWorkoutWithExercises(workout: Workout, exercises: List<Exercise>) {
        workoutDao.update(workout)

        workoutDao.deleteCrossRefsForWorkout(workout.workoutId)

        exercises.forEach { exercise ->
            val exerciseId = if (exercise.exerciseId == 0L) {
                exerciseDao.insert(exercise)
            } else {
                exercise.exerciseId
            }

            val crossRef = WorkoutExerciseCrossRef(workout.workoutId, exerciseId)
            workoutDao.insertCrossRef(crossRef)
        }
    }

    suspend fun deleteWorkout(workoutWithExercises: WorkoutWithExercises) {
        val workout = workoutWithExercises.workout
        workoutDao.deleteCrossRefsForWorkout(workout.workoutId)
        workoutDao.delete(workout)
    }

}