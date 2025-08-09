package com.example.workmysets.data.repositories

import androidx.lifecycle.LiveData
import com.example.workmysets.data.dao.UserDao
import com.example.workmysets.data.entities.user.entity.User

class UserRepository(val dao: UserDao) {
    val allUsers = dao.getAll()

    suspend fun insert(user: User) {
        dao.insert(user)
    }

    suspend fun update(user: User) {
        dao.update(user)
    }

    suspend fun delete(user: User) {
        dao.delete(user)
    }

    fun hasUsers(): LiveData<Boolean> {
        return dao.hasUsers()
    }

}