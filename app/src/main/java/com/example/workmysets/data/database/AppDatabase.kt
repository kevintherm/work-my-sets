package com.example.workmysets.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.workmysets.data.converter.Converters
import com.example.workmysets.data.dao.ExerciseDao
import com.example.workmysets.data.dao.ScheduleDao
import com.example.workmysets.data.dao.SessionDao
import com.example.workmysets.data.dao.WorkoutDao
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.schedule.entity.Schedule
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.relation.SessionWorkoutCrossRef
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.entities.workout.relation.WorkoutExerciseCrossRef
import com.example.workmysets.data.samples.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Schedule::class,
        Session::class,
        Workout::class,
        Exercise::class,
        WorkoutExerciseCrossRef::class,
        SessionWorkoutCrossRef::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun SessionDao(): SessionDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).exerciseDao().insertAll(SampleData.exercises)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
