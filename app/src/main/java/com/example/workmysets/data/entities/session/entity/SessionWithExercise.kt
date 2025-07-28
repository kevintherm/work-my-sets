package com.example.workmysets.data.entities.session.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.example.workmysets.data.entities.exercise.entity.Exercise

data class SessionWithExercise(
    @Embedded val session: Session,

    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "exerciseId"
    )
    val exercise: Exercise
)