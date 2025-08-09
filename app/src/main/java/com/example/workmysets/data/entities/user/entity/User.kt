package com.example.workmysets.data.entities.user.entity

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.io.FileOutputStream

@Entity(tableName = "users")
data class User(
    val name: String,
    val age: Int,
    val gender: String,
    val weight: Float,
    val height: Float,
    val profilePath: String?
) {
    @PrimaryKey(autoGenerate = true)
    var userId = -1

    companion object {
        fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): String {
            val filename = "profile_${System.currentTimeMillis()}.png"
            val file = File(context.filesDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            return file.absolutePath
        }
    }
}
