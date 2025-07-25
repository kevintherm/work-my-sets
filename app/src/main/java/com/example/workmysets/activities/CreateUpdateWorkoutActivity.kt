package com.example.workmysets.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.adapters.ExerciseCheckboxAdapter
import com.example.workmysets.data.models.Exercise
import com.example.workmysets.data.models.Schedule
import com.example.workmysets.data.models.Workout
import com.example.workmysets.data.repositories.ExerciseRepository
import com.example.workmysets.data.repositories.WorkoutRepository
import com.example.workmysets.data.viewmodels.ExerciseViewModel
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.ActivityCreateUpdateWorkoutBinding
import com.example.workmysets.databinding.ActivityMainBinding
import com.example.workmysets.ui.interfaces.ImplementBackButton
import com.example.workmysets.utils.Consts
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.saadahmedev.popupdialog.PopupDialog
import kotlinx.coroutines.launch

class CreateUpdateWorkoutActivity : AppCompatActivity(), ImplementBackButton {
    private lateinit var binding: ActivityCreateUpdateWorkoutBinding

    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var adapter: ExerciseCheckboxAdapter

    private val exercises = mutableSetOf<Exercise>()
    private val selectedExercises = mutableSetOf<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateUpdateWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        exerciseViewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]
        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class.java]
        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        if (intent.hasExtra(Consts.ARG_WORKOUT_ID)) {
            binding.topBar.titleText.text = "Update Workout"
            workoutViewModel.findById(intent.getLongExtra(Consts.ARG_WORKOUT_ID, -1))
        } else {
            binding.topBar.titleText.text = "Create Workout"
        }

        binding.topBar.backButton.visibility = View.VISIBLE
        binding.topBar.backButton.setOnClickListener { triggerBackButton() }
        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.setImageResource(R.drawable.ic_save_as)
        binding.topBar.actionButton.setOnClickListener { saveWorkout() }

        adapter = ExerciseCheckboxAdapter(mutableListOf(), selectedExercises)
        binding.exerciseRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.exerciseRecyclerView.adapter = adapter

        exerciseViewModel.allExercises.observe(this) { list ->
            exercises.clear()
            exercises.addAll(list)
            adapter.updateData(list)

        }

        exerciseViewModel.searchResult.observe(this) { filtered ->
            adapter.updateData(filtered)
        }

        workoutViewModel.selectedWorkout.observe(this) { workoutWithExercises ->
            if (workoutWithExercises != null) {
                binding.nameInput.setText(workoutWithExercises.workout.name)
                binding.descriptionInput.setText(workoutWithExercises.workout.description)
                selectedExercises.clear()
                selectedExercises.addAll(workoutWithExercises.exercises)
                adapter.notifyDataSetChanged()
            } else {
                PopupDialog.getInstance(this)
                    .statusDialogBuilder()
                    .createErrorDialog()
                    .setHeading(getString(R.string.failed))
                    .setDescription(getString(R.string.workout_not_found))
                    .setActionButtonText(getString(R.string.okay))
                    .build({
                        val i = Intent(this, MainActivity::class.java).apply {
                            putExtra(Consts.ARG_REDIRECT_PAGE, 1)
                        }
                        startActivity(i)
                        finish()
                    })
                    .show()
            }
        }

        binding.searchExerciseInput.doOnTextChanged { text, _, _, _ ->
            exerciseViewModel.searchExercises(text.toString())
        }

    }

    private fun saveWorkout() {
        val workoutId = intent.getLongExtra(Consts.ARG_WORKOUT_ID, -1)

        val name = binding.nameInput.text.toString()
        val desc = binding.descriptionInput.text.toString()

        if (name.isBlank()) {
            PopupDialog.getInstance(this)
                .statusDialogBuilder()
                .createWarningDialog()
                .setHeading(getString(R.string.oops))
                .setDescription(getString(R.string.workout_name_cannot_be_empty))
                .setActionButtonText(getString(R.string.okay))
                .build(Dialog::dismiss)
                .show()
            return
        }

        if (selectedExercises.size <= 0) {
            PopupDialog.getInstance(this)
                .statusDialogBuilder()
                .createWarningDialog()
                .setHeading(getString(R.string.oops))
                .setDescription(getString(R.string.please_select_at_least_1_exercise))
                .setActionButtonText(getString(R.string.okay))
                .build(Dialog::dismiss)
                .show()
            return
        }

        lifecycleScope.launch {
            var schedule = scheduleViewModel.getSchedule()
            if (schedule == null) {
                schedule = Schedule("Default Schedule")
                scheduleViewModel.createSchedule(schedule)
            }

            val workout = Workout(name, desc)
            if (workoutId != -1L) workout.workoutId = workoutId

            workoutViewModel.insertWorkoutWithExercises(workout, selectedExercises.toList())

            PopupDialog.getInstance(this@CreateUpdateWorkoutActivity)
                .statusDialogBuilder()
                .createSuccessDialog()
                .setHeading(getString(R.string.success))
                .setDescription(getString(R.string.workout_saved))
                .setActionButtonText(getString(R.string.okay))
                .build {
                    val i =
                        Intent(this@CreateUpdateWorkoutActivity, MainActivity::class.java).apply {
                            putExtra(Consts.ARG_REDIRECT_PAGE, 1)
                        }
                    startActivity(i)
                    finish()
                }
                .show()
        }
    }

    override fun triggerBackButton() {
        onBackPressedDispatcher.onBackPressed()
    }
}