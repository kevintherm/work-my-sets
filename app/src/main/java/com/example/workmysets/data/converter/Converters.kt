package com.example.workmysets.data.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromIntList(value: List<Int>?): String = value?.joinToString(",") ?: ""

    @TypeConverter
    fun toIntList(value: String): List<Int> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.toInt() }

    @TypeConverter
    fun fromFloatList(value: List<Float>?): String = value?.joinToString(",") ?: ""

    @TypeConverter
    fun toFloatList(value: String): List<Float> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.toFloat() }

    @TypeConverter
    fun fromLongList(value: List<Long>?): String = value?.joinToString(",") ?: ""

    @TypeConverter
    fun toLongList(value: String): List<Long> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.toLong() }
}
