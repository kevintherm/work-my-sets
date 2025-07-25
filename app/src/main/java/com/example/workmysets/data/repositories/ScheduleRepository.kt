package com.example.workmysets.data.repositories

import com.example.workmysets.data.dao.ScheduleDao
import com.example.workmysets.data.entities.schedule.entity.Schedule

class ScheduleRepository(private val dao: ScheduleDao) {

    val scheduleWithWorkouts = dao.getScheduleWithWorkouts()

    suspend fun scheduleExists(): Boolean {
        return dao.getSchedule() != null
    }

    suspend fun getSchedule(): Schedule? {
        return dao.getSchedule()
    }

    suspend fun insert(schedule: Schedule): Long {
        return dao.insert(schedule)
    }

    suspend fun delete(schedule: Schedule): Int {
        return dao.delete(schedule)
    }

}