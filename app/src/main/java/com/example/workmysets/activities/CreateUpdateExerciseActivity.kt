package com.example.workmysets.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.workmysets.R
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.viewmodels.ExerciseViewModel
import com.example.workmysets.databinding.ActivityCreateUpdateExerciseBinding
import com.example.workmysets.databinding.ActivityCreateUpdateWorkoutBinding
import com.example.workmysets.utils.Consts
import com.saadahmedev.popupdialog.PopupDialog

class CreateUpdateExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateUpdateExerciseBinding

    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private var loadedExercise: Exercise? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateUpdateExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topBar.backButton.visibility = View.VISIBLE
        binding.topBar.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.setImageResource(R.drawable.ic_save_as)
        binding.topBar.actionButton.setOnClickListener { saveExercise() }

        if (intent.hasExtra(Consts.ARG_EXERCISE_ID)) {
            binding.topBar.titleText.text = "Update Exercise"

            val exerciseId = intent.getLongExtra(Consts.ARG_EXERCISE_ID, -1)

            exerciseViewModel.findById(exerciseId).observe(this) {
                if (it == null) {
                    Toast.makeText(this, "Invalid exercise.", Toast.LENGTH_LONG).show()
                    finish()
                    return@observe
                }

                loadedExercise = it

                binding.nameInput.setText(it.name)
                binding.targetMuscle.setText(it.targetMuscle)
                binding.calorieBurned.setText(it.calorieBurned.toString())
                binding.youtubeVideoId.setText(
                    it.youtubeVideoId?.replace("https://www.youtube.com/watch?v=", "")
                        ?: ""
                )

                binding.loading.visibility = View.GONE
            }

        } else {
            binding.topBar.titleText.text = "Create Exercise"
            binding.loading.visibility = View.GONE
        }
    }

    private fun saveExercise() {
        val name = binding.nameInput.text.toString()
        val muscle = binding.targetMuscle.text.toString()
        val calorie = binding.calorieBurned.text.toString()
        val youtubeVid =
            binding.youtubeVideoId.text.toString().replace("https://www.youtube.com/watch?v=", "")

        val missingFields = mutableListOf<String>()

        if (name.isBlank()) missingFields.add("Name")
        if (muscle.isBlank()) missingFields.add("Target Muscle")
        if (calorie.isBlank()) missingFields.add("Calorie Burned")

        if (missingFields.isNotEmpty()) {
            val fieldsString = missingFields.joinToString(", ")
            PopupDialog.getInstance(this).statusDialogBuilder()
                .createWarningDialog().setHeading(getString(R.string.oops))
                .setDescription("Please fill the following fields: $fieldsString.")
                .setActionButtonText(getString(R.string.okay)).build(Dialog::dismiss).show()
            return
        }

        val exercise = Exercise(name, muscle, calorie.toDouble(), youtubeVid)

        if (loadedExercise != null) {
            exercise.exerciseId = loadedExercise!!.exerciseId
            exerciseViewModel.update(exercise)
        } else {
            exerciseViewModel.insert(exercise)
        }

        PopupDialog.getInstance(this)
            .statusDialogBuilder()
            .createSuccessDialog()
            .setCancelable(false)
            .setHeading(getString(R.string.success))
            .setDescription("Exercise saved!")
            .setActionButtonText(getString(R.string.okay))
            .build {
                Intent(this, MainActivity::class.java).apply {
                    putExtra(Consts.ARG_REDIRECT_PAGE, 1)
                }.also {
                    startActivity(it)
                    finish()
                }
            }
            .show()
    }
}