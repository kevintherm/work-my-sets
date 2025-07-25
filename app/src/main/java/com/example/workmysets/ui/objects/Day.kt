package com.example.workmysets.ui.objects

data class Day(
    var key: Int,
    var label: String
) {
    companion object {
        fun getWeekDays(): List<Day> {
            val days = listOf(
                Day(-1, "Unassigned"),
                Day(1, "Monday"),
                Day(2, "Tuesday"),
                Day(3, "Wednesday"),
                Day(4, "Thursday"),
                Day(5, "Friday"),
                Day(6, "Saturday"),
                Day(7, "Sunday"),
            )
            return days
        }
    }

    override fun toString(): String = label
}
