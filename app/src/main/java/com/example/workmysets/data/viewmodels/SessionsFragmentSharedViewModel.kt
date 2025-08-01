package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.workmysets.data.entities.workout.entity.Workout

class SessionsFragmentSharedViewModel(application: Application) : AndroidViewModel(application) {

    val dateValues = listOf(
        -1, 0, 1, 7, 14, 30, 120, 365
    )
    val dateLabels = listOf(
        "All", "Today", "Yesterday", "Last week", "Last 2 weeks", "Last month", "Last 6 months", "Last year"
    )

    val sort = MutableLiveData<String>()
    val isCompleted = MutableLiveData<String>()
    val filterDate = MutableLiveData<String>()
    val workout = MutableLiveData<Workout>()

    private val parameters = mapOf(
        "sort" to sort,
        "completed" to isCompleted,
        "date" to filterDate,
        "workout" to workout
    )

    operator fun get(key: String): MutableLiveData<out Any> {
        return parameters[key] ?: throw IllegalArgumentException("Invalid key: $key")
    }

    fun clearParameter(key: String) {
        val liveData = this[key]
        if (liveData is MutableLiveData<*>) {
            if (liveData.value is String) {
                (liveData as MutableLiveData<String>).value = ""
            } else if (liveData.value is Workout) {
                (liveData as MutableLiveData<Workout>).postValue(null)
            }
        }
    }
}
