package com.example.workmysets.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.entities.user.entity.User
import com.example.workmysets.data.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    val allUsers: LiveData<List<User>>
    val repository: UserRepository

    init {
        val dao = AppDatabase.getDatabase(application).UserDao()
        repository = UserRepository(dao)
        allUsers = repository.allUsers
    }

    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }

    fun update(user: User) = viewModelScope.launch {
        repository.update(user)
    }

    fun delete(user: User) = viewModelScope.launch {
        repository.delete(user)
    }

    fun hasUsers(): LiveData<Boolean> {
        return repository.hasUsers()
    }

}