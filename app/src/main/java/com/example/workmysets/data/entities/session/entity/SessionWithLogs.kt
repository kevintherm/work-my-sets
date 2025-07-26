package com.example.workmysets.data.entities.session.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithLogs(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val logs: List<SessionExerciseLog>
)
