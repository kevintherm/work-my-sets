package com.example.workmysets.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workmysets.R
import com.example.workmysets.adapters.ExerciseTrackAdapter
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.schedule.entity.Schedule
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.ActivityTrackSessionBinding
import com.example.workmysets.utils.Consts
import kotlinx.coroutines.launch

class TrackSessionActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityTrackSessionBinding

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var sessionViewModel: SessionViewModel

    private lateinit var exerciseTrackAdapter: ExerciseTrackAdapter

    private lateinit var workout: WorkoutWithExercises

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTrackSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class]
        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class]
        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class]

        exerciseTrackAdapter = ExerciseTrackAdapter()
        exerciseTrackAdapter.onButtonPlayClick = this::onClickPlayButton

        binding.topBar.titleText.text = "Track Session"
        binding.topBar.backButton.visibility = View.VISIBLE
        binding.topBar.backButton.setOnClickListener(this)
        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.setImageResource(R.drawable.ic_session)
        binding.topBar.actionButton.isClickable = false
        binding.topBar.actionButton.isFocusable = false

        binding.workoutsRecycler.adapter = exerciseTrackAdapter
        binding.workoutsRecycler.layoutManager = LinearLayoutManager(this)

        workoutViewModel.selectedWorkout.observe(this) { workoutWithExercises ->
            Log.d("WORKOUT VIEW MODEL OBSERVER", "data: $workoutWithExercises")
            if (workoutWithExercises == null) {
                Intent(this@TrackSessionActivity, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
                return@observe
            }

            workout = workoutWithExercises
            exerciseTrackAdapter.updateList(workoutWithExercises.exercises)
        }

        scheduleViewModel.scheduleWithWorkouts.observe(this) { scheduleWithWorkouts ->
            Log.d("SCHEDULE VIEW MODEL OBSERVER", "data: $scheduleWithWorkouts")
            if (scheduleWithWorkouts == null || scheduleWithWorkouts.workouts.isEmpty()) {
                Intent(this@TrackSessionActivity, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
                return@observe
            }

            val firstWorkout = scheduleWithWorkouts.workouts[0]
            workoutViewModel.findById(firstWorkout.workoutId)
            binding.topBar.titleText.text = firstWorkout.name
        }

    }

    private fun onClickPlayButton(exercise: Exercise) {
        val intent = Intent(this, RealtimeExerciseActivity::class.java).apply {
            putExtra(Consts.ARG_EXERCISE_ID, exercise.exerciseId)
            putExtra(Consts.ARG_WORKOUT_ID, workout.workout.workoutId)
        }
        startActivity(intent)
    }

    override fun onClick(button: View?) {
        when (button?.id) {
            R.id.backButton -> onBackPressedDispatcher.onBackPressed()
        }
    }
}