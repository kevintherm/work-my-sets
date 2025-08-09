package com.example.workmysets.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel() : ViewModel() {

    val name = MutableLiveData<String>()
    val age = MutableLiveData<Int>()
    val gender = MutableLiveData<String>()
    val weight = MutableLiveData<Float>()
    val height = MutableLiveData<Float>()
    val profilePath = MutableLiveData<String>()

}