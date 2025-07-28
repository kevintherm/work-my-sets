package com.example.workmysets.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.entities.workout.relation.WorkoutExerciseCrossRef
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: WorkoutExerciseCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyCrossRef(crossRefs: List<WorkoutExerciseCrossRef>)

    @Delete
    suspend fun delete(workout: Workout)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: Workout)

    @Transaction
    @Query("SELECT * FROM workouts WHERE workoutId = :id LIMIT 1")
    fun getWorkoutWithExercises(id: Long): LiveData<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM workouts")
    fun getAllWorkoutsWithExercises(): LiveData<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE name LIKE '%' || :search || '%'")
    fun findByName(search: String): LiveData<List<WorkoutWithExercises>>

    @Query("DELETE FROM workout_exercise_cross_ref WHERE workoutId = :workoutId")
    suspend fun deleteCrossRefsForWorkout(workoutId: Long)

}