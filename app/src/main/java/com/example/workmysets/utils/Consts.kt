package com.example.workmysets.utils

import android.app.Activity
import android.content.Intent
import com.example.workmysets.activities.ConfigureScheduleActivity
import com.example.workmysets.activities.CreateUpdateWorkoutActivity
import com.example.workmysets.activities.MainActivity
import com.example.workmysets.activities.TrackSessionActivity
import com.example.workmysets.activities.WidgetsActivity
import com.example.workmysets.ui.components.SearchItem

class Consts {
    companion object {
        const val ARG_SESSION_ID = "workout_id"
        const val ARG_WORKOUT_ID = "workout_id"
        const val ARG_EXERCISE_ID = "exercise_id"
        const val ARG_DAY_OF_WEEK = "workout_day_of_week"
        const val ARG_REDIRECT_PAGE = "redirect_page"

        fun getShortcutsSearchList(): List<SearchItem> {
            return listOf(
                SearchItem("Stats") { ctx ->
                    ctx.startActivity(Intent(ctx, WidgetsActivity::class.java))
                },
                SearchItem("Configure Schedule") { ctx ->
                    ctx.startActivity(Intent(ctx, ConfigureScheduleActivity::class.java))
                },
                SearchItem("Sessions History") { ctx ->
                    ctx.startActivity(Intent(ctx, MainActivity::class.java).apply {
                        putExtra(
                            ARG_REDIRECT_PAGE, 2
                        )
                    })
                    (ctx as? Activity)?.finish()
                },
                SearchItem("Create Workout") { ctx ->
                    ctx.startActivity(Intent(ctx, CreateUpdateWorkoutActivity::class.java))
                },
                SearchItem("Current Workout List") { ctx ->
                    ctx.startActivity(Intent(ctx, TrackSessionActivity::class.java))
                }
            )
        }
    }
}