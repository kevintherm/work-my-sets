package com.example.workmysets.data.objects

data class Day(
    var key: Int,
    var label: String
) {
    companion object {
        val DAYS = listOf(
            Day(-1, "Unassigned"),
            Day(1, "Monday"),
            Day(2, "Tuesday"),
            Day(3, "Wednesday"),
            Day(4, "Thursday"),
            Day(5, "Friday"),
            Day(6, "Saturday"),
            Day(7, "Sunday"),
        )

        fun getWeekDays(): List<Day> {
            return DAYS
        }

        fun getDay(dayInt: Int): String {
            return when (dayInt) {
                -1 -> "Unassigned"
                1 -> "Monday"
                2 -> "Tuesday"
                3 -> "Wednesday"
                4 -> "Thursday"
                5 -> "Friday"
                6 -> "Saturday"
                7 -> "Sunday"
                else -> "Unassigned"
            }
        }
    }

    override fun toString(): String = label
}
