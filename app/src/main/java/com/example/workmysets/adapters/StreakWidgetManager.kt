package com.example.workmysets.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import com.example.workmysets.R
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.databinding.StreakComponentBinding

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class StreakWidgetManager(
    private val context: Context,
    private val binding: StreakComponentBinding
) {

    private data class StreakInfo(
        val streakDays: Int,
        val isTodayCompleted: Boolean,
        val hasWorkoutScheduledToday: Boolean
    )

    fun updateWidget(allWorkouts: List<Workout>, allSessions: List<Session>) {
        val completedSessions = allSessions.filter { it.isCompleted && it.endsAt.isNotBlank() }
        if (completedSessions.isEmpty()) {
            setupInitialState(allWorkouts)
            return
        }

        val streakInfo = calculateStreak(completedSessions, allWorkouts)

//        binding.checksBackground.visibility =
//            if (streakInfo.streakDays > 0) View.VISIBLE else View.INVISIBLE

        updateStreakCountAndMessage(streakInfo)
        updateWeeklyDayIndicators(allWorkouts, completedSessions, streakInfo)
        updateMainImageAndFireIcon(streakInfo)
    }

    private fun calculateStreak(
        completedSessions: List<Session>,
        allWorkouts: List<Workout>
    ): StreakInfo {
        val completedDates = completedSessions.map {
            Instant.parse(it.endsAt).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSet()

        var streakCount = 0
        var currentCheckDate = LocalDate.now()
        val today = LocalDate.now()

        if (completedDates.contains(today)) {
            streakCount++
            currentCheckDate = today.minusDays(1)
        } else {
            currentCheckDate = today.minusDays(1)
        }

        while (completedDates.contains(currentCheckDate)) {
            streakCount++
            currentCheckDate = currentCheckDate.minusDays(1)
        }

        val todayDayOfWeek = today.dayOfWeek.value // Monday=1, Sunday=7
        val hasWorkoutToday = allWorkouts.any { it.dayOfWeek == todayDayOfWeek }

        return StreakInfo(
            streakDays = streakCount,
            isTodayCompleted = completedDates.contains(today),
            hasWorkoutScheduledToday = hasWorkoutToday
        )
    }

    private fun updateStreakCountAndMessage(streakInfo: StreakInfo) {
        binding.mainWidgetValue.text = "${streakInfo.streakDays} days"

        binding.mainWidgetBottomText.text = when {
            streakInfo.hasWorkoutScheduledToday && !streakInfo.isTodayCompleted ->
                "Come back for another session!"

            streakInfo.streakDays > 0 ->
                "Great job! Keep the fire burning!"

            else ->
                "Complete a session to start a new streak!"
        }
    }

    private fun updateMainImageAndFireIcon(streakInfo: StreakInfo) {
        // A streak is considered "broken" or "waiting" if theres a workout for today
        // that hasn't been completed yet.
        val needsActionToday = streakInfo.hasWorkoutScheduledToday && !streakInfo.isTodayCompleted

        if (streakInfo.streakDays == 0 || needsActionToday) {
            // State: No streak or streak is pending for today -> Motivate user
            binding.streakFire.setColorFilter(
                ContextCompat.getColor(context, R.color.secondary),
                PorterDuff.Mode.MULTIPLY
            )
            binding.mainImage.setImageResource(R.drawable.ic_fitness_4)

        } else {
            // State: Streak is active and up-to-date -> "Chill" state
            binding.streakFire.clearColorFilter() // Remove tint
            binding.mainImage.setImageResource(R.drawable.ic_chill_2)
        }
    }

    private fun updateWeeklyDayIndicators(
        allWorkouts: List<Workout>,
        completedSessions: List<Session>,
        streakInfo: StreakInfo
    ) {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val scheduledDays = allWorkouts.associateBy { it.dayOfWeek }
        val completedDatesThisWeek = completedSessions
            .map { Instant.parse(it.endsAt).atZone(ZoneId.systemDefault()).toLocalDate() }
            .filter { !it.isBefore(startOfWeek) && !it.isAfter(startOfWeek.plusDays(6)) }
            .toSet()

        val dayTextViews = listOf(
            binding.streakMonday,
            binding.streakTuesday,
            binding.streakWednesday,
            binding.streakThursday,
            binding.streakFriday,
            binding.streakSaturday,
            binding.streakSunday
        )

        dayTextViews.forEachIndexed { index, textView ->
            val dayIndex = index + 1
            val currentDate = startOfWeek.plusDays(index.toLong())
            val isScheduled = scheduledDays.containsKey(dayIndex)
            val isCompleted = completedDatesThisWeek.contains(currentDate)
            val isToday = currentDate.isEqual(today)

            val canContinueStreakOnRestDay =
                isToday && !isScheduled && streakInfo.streakDays > 0 && !streakInfo.isTodayCompleted

            val drawableId = when {
                isCompleted -> R.drawable.ic_check
                canContinueStreakOnRestDay -> R.drawable.ic_circle
                isScheduled -> R.drawable.ic_circle
                else -> R.drawable.ic_moon
            }

            val drawable = ContextCompat.getDrawable(context, drawableId)?.mutate()
            drawable?.setTint(ContextCompat.getColor(context, R.color.white))
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
        }
    }

    private fun setupInitialState(allWorkouts: List<Workout>) {
        val streakInfo = StreakInfo(
            0,
            isTodayCompleted = false,
            hasWorkoutScheduledToday = allWorkouts.any { it.dayOfWeek == LocalDate.now().dayOfWeek.value })
        updateStreakCountAndMessage(streakInfo)
        updateWeeklyDayIndicators(allWorkouts, emptyList(), streakInfo)
        updateMainImageAndFireIcon(streakInfo)
//        binding.checksBackground.visibility = View.INVISIBLE
    }
}