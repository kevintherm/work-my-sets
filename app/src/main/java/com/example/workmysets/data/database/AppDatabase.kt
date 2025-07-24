package com.example.workmysets.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.workmysets.data.dao.ExerciseDao
import com.example.workmysets.data.dao.SessionDao
import com.example.workmysets.data.dao.WorkoutDao
import com.example.workmysets.data.models.Exercise
import com.example.workmysets.data.models.Session
import com.example.workmysets.data.models.SessionWorkoutCrossRef
import com.example.workmysets.data.models.Workout
import com.example.workmysets.data.models.WorkoutExerciseCrossRef
import com.example.workmysets.data.sample.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Session::class, Workout::class, Exercise::class, WorkoutExerciseCrossRef::class, SessionWorkoutCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
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
