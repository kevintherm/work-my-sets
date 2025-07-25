package com.example.workmysets.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workmysets.R
import com.example.workmysets.adapters.WorkoutConfigureAdapter
import com.example.workmysets.data.models.Schedule
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.ActivityConfigureScheduleBinding
import com.example.workmysets.databinding.ActivityCreateUpdateWorkoutBinding
import com.example.workmysets.utils.Consts
import com.saadahmedev.popupdialog.PopupDialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ConfigureScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigureScheduleBinding

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var workoutAdapter: WorkoutConfigureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfigureScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class]
        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class]

        binding.topBar.titleText.text = "Configure Schedule"
        binding.topBar.backButton.visibility = View.VISIBLE
        binding.topBar.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.setImageResource(R.drawable.ic_save_as)
        binding.topBar.actionButton.setOnClickListener { lifecycleScope.launch { saveSchedule() } }

        scheduleViewModel.scheduleWithWorkouts.observe(this) {
            if (it == null) {
                val intent = Intent(this@ConfigureScheduleActivity, MainActivity::class.java)
                    .apply {
                        putExtra(Consts.ARG_REDIRECT_PAGE, 1)
                    }
                startActivity(intent)
                finish()
                return@observe
            }

            binding.scheduleProgress.progress = 0
            binding.scheduleProgressText.text = "0%"
            binding.scheduleNameText.text = it.schedule.name

            val dayFormatter = DateTimeFormatter.ofPattern("EEEE")
            val day = LocalDate.now().format(dayFormatter)
            binding.schedulePlanText.text = "$day - Pull Day"

            binding.scheduleCard.isClickable = false
            binding.scheduleCard.isFocusable = false

            binding.nameInput.setText(it.schedule.name)

            workoutAdapter = WorkoutConfigureAdapter()
            binding.workoutsRecycler.layoutManager = LinearLayoutManager(this)
            binding.workoutsRecycler.adapter = workoutAdapter

            scheduleViewModel.scheduleWithWorkouts.observe(this) { scheduleWithWorkouts ->
                workoutAdapter.submitList(scheduleWithWorkouts.workouts)
            }

        }
    }

    private suspend fun saveSchedule() {
//        save schedule details
        val name = binding.nameInput.text.toString()
        val schedule = Schedule(name)
        scheduleViewModel.createSchedule(schedule)

//        save workouts day of week details
        val workouts = workoutAdapter.items
        workouts.forEach {
            workoutViewModel.update(it)
        }

        PopupDialog.getInstance(this@ConfigureScheduleActivity)
            .statusDialogBuilder()
            .createSuccessDialog()
            .setHeading(getString(R.string.success))
            .setDescription(getString(R.string.configuration_saved))
            .setActionButtonText(getString(R.string.okay))
            .build {
                val i = Intent(this@ConfigureScheduleActivity, MainActivity::class.java)
                    .apply { putExtra(Consts.ARG_REDIRECT_PAGE, 1) }
                startActivity(i)
                finish()
            }
            .show()
    }
}