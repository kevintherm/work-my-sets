package com.example.workmysets.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workmysets.R
import com.example.workmysets.adapters.ExerciseTrackAdapter
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.ActivityTrackSessionBinding
import com.example.workmysets.utils.Consts
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener

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

        scheduleViewModel.scheduleWithWorkouts.observe(this) { scheduleWithWorkouts ->
            if (scheduleWithWorkouts == null || scheduleWithWorkouts.workouts.isEmpty()) {
                Toast.makeText(this@TrackSessionActivity, "Invalid schedule", Toast.LENGTH_SHORT)
                    .show()
                finish()
                return@observe
            }

            val workoutFind = scheduleWithWorkouts.workouts[0]
            binding.topBar.titleText.text = workoutFind.name

            workoutViewModel.findById(workoutFind.workoutId).observe(this) {
                if (it == null) {
                    Toast.makeText(this@TrackSessionActivity, "Invalid workout", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                    return@observe
                }

                workout = it
                exerciseTrackAdapter.updateList(it.exercises)
            }
        }

    }

    private fun onClickPlayButton(exercise: Exercise) {
        val dialog = PopupDialog.getInstance(this)
            .standardDialogBuilder()
            .createStandardDialog()
            .setHeading(exercise.name)
            .setDescription(getString(R.string.start_exercise, exercise.name))
            .setIcon(R.drawable.ic_question)
            .setIconColor(R.color.primary)
            .setCancelable(false)
            .setNegativeButtonCornerRadius(16F)
            .setPositiveButtonCornerRadius(16F)
            .setPositiveButtonBackgroundColor(R.color.primary)
            .setPositiveButtonTextColor(R.color.white)
            .setPositiveButtonText(getString(R.string.confirm))
            .build(object : StandardDialogActionListener {
                override fun onPositiveButtonClicked(dialog: Dialog) {
                    gotoTrackExerciseActivity(exercise)
                    dialog.dismiss()
                }

                override fun onNegativeButtonClicked(dialog: Dialog) {
                    dialog.dismiss()
                }
            })

        dialog.show()
    }

    private fun gotoTrackExerciseActivity(exercise: Exercise) {
        val intent = Intent(this, SessionExerciseActivity::class.java).apply {
            putExtra(Consts.ARG_EXERCISE_ID, exercise.exerciseId)
            putExtra(Consts.ARG_WORKOUT_ID, workout.workout.workoutId)
        }
        startActivity(intent)
        finish()
    }

    override fun onClick(button: View?) {
        when (button?.id) {
            R.id.backButton -> onBackPressedDispatcher.onBackPressed()
        }
    }
}