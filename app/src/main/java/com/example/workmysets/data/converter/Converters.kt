package com.example.workmysets.data.converter

import androidx.room.TypeConverter
import com.example.workmysets.data.objects.TimestampPair
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant

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

    // ✅ Instant <-> String converter (ISO 8601)
    @TypeConverter
    fun fromInstant(value: Instant?): String? = value?.toString()

    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let { Instant.parse(it) }

    @TypeConverter
    fun fromTimestampPairList(value: List<TimestampPair>): String =
        Gson().toJson(value)

    @TypeConverter
    fun toTimestampPairList(value: String): List<TimestampPair> {
        val type = object : TypeToken<List<TimestampPair>>() {}.type
        return Gson().fromJson(value, type)
    }

}
