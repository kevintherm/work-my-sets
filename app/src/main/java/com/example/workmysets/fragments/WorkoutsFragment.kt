package com.example.workmysets.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.activities.ConfigureScheduleActivity
import com.example.workmysets.activities.CreateUpdateExerciseActivity
import com.example.workmysets.activities.CreateUpdateWorkoutActivity
import com.example.workmysets.adapters.WorkoutAdapter
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.schedule.entity.ScheduleWithWorkouts
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.viewmodels.ExerciseViewModel
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentWorkoutsBinding
import com.example.workmysets.utils.Consts
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class WorkoutsFragment : Fragment() {
    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private val exerciseViewModel: ExerciseViewModel by activityViewModels()

    private var schedule: ScheduleWithWorkouts? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScheduleSection()
        setupWorkoutsSection()
        setupExercisesSection()

    }

    private fun setupExercisesSection() {
        class ExerciseAdapter : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
            val items = mutableListOf<Exercise>()
            var onClick: ((Exercise) -> Unit)? = null

            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val exerciseName: TextView = view.findViewById(R.id.exerciseName)
                val experienceCount: TextView = view.findViewById(R.id.experience)

                fun bind(item: Exercise) {
                    onClick?.invoke(item)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.exercise_item_recycler, parent, false)
                return ViewHolder(view)
            }

            override fun getItemCount(): Int = items.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = items[position]

                holder.exerciseName.text = item.name
                holder.experienceCount.text = "0 Sets"
                holder.itemView.setOnClickListener {
                    holder.bind(item)
                }
            }

            fun updateList(newList: List<Exercise>) {
                items.clear()
                items.addAll(newList)
                notifyDataSetChanged()
            }

        }

        binding.topBarExercisesSection.titleText.text = "Exercises"
        binding.topBarExercisesSection.actionButton.visibility = View.VISIBLE
        binding.topBarExercisesSection.actionButton.setImageResource(R.drawable.ic_add)
        binding.topBarExercisesSection.actionButton.setOnClickListener {
            val intent = Intent(context, CreateUpdateExerciseActivity::class.java)
            startActivity(intent)
        }

        val adapter = ExerciseAdapter()
        binding.exercisesRecycler.adapter = adapter
        binding.exercisesRecycler.layoutManager = LinearLayoutManager(requireActivity())

        adapter.onClick = { exercise ->
            val intent = Intent(requireContext(), CreateUpdateExerciseActivity::class.java).apply {
                putExtra(Consts.ARG_EXERCISE_ID, exercise.exerciseId)
            }
            startActivity(intent)
        }

        exerciseViewModel.allExercises.observe(viewLifecycleOwner) { exercises ->
            if (exercises.isEmpty()) {
                binding.exercisesRecyclerEmpty.visibility = View.VISIBLE
            } else {
                binding.exercisesRecyclerEmpty.visibility = View.GONE
            }

            adapter.updateList(exercises)
        }
    }

    private fun setupScheduleSection() {
        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        binding.topBarScheduleSection.titleText.text = "Schedules"

        scheduleViewModel.scheduleWithWorkouts.observe(viewLifecycleOwner) { scheduleWithWorkouts ->
            if (scheduleWithWorkouts == null) {
                binding.scheduleProgress.progress = 0
                binding.scheduleProgressText.text = "0%"
                binding.scheduleNameText.text = "Your Schedule"
                binding.schedulePlanText.text = "Create a workout to configure your schedule..."
                binding.scheduleCard.isClickable = false
                binding.scheduleCard.isFocusable = false
                return@observe
            }

            binding.scheduleNameText.text = scheduleWithWorkouts.schedule.name

            val now = LocalDate.now()
            val dayString = now.format(DateTimeFormatter.ofPattern("EEEE"))

            val todaysWorkout = scheduleWithWorkouts.workouts
                .filter { it.dayOfWeek == now.dayOfWeek.value }
                .joinToString(", ") { it.name }

            val text = if (todaysWorkout.isNotEmpty()) "$dayString - $todaysWorkout"
            else "$dayString - No workouts for today!"

            binding.schedulePlanText.text = text

            val weeklyProgress = ((now.dayOfWeek.value.toDouble() / 7) * 100).toInt()
            binding.scheduleProgress.progress = weeklyProgress
            binding.scheduleProgressText.text = "$weeklyProgress%"

            binding.scheduleCard.isClickable = true
            binding.scheduleCard.isFocusable = true

            binding.scheduleCard.setOnClickListener {
                val intent = Intent(requireContext(), ConfigureScheduleActivity::class.java)
                startActivity(intent)

            }
        }
    }

    private val onClickEdit: (WorkoutWithExercises) -> Unit = { workoutWithExercises ->
        val intent = Intent(requireContext(), CreateUpdateWorkoutActivity::class.java).apply {
            putExtra(Consts.ARG_WORKOUT_ID, workoutWithExercises.workout.workoutId)
            putExtra(Consts.ARG_DAY_OF_WEEK, workoutWithExercises.workout.dayOfWeek)
        }
        startActivity(intent)
    }

    private fun setupWorkoutsSection() {
        binding.topBarWorkoutsSection.titleText.text = "Workouts"

        binding.topBarWorkoutsSection.actionButton.visibility = View.VISIBLE
        binding.topBarWorkoutsSection.actionButton.setImageResource(R.drawable.ic_add)
        binding.topBarWorkoutsSection.actionButton.setOnClickListener {
            val intent = Intent(context, CreateUpdateWorkoutActivity::class.java)
            startActivity(intent)
        }

        val workoutAdapter = WorkoutAdapter(onClickEdit)
        binding.workoutsRecycler.layoutManager = LinearLayoutManager(context)
        binding.workoutsRecycler.adapter = workoutAdapter

        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class.java]
        workoutViewModel.allWorkouts.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.workoutsRecyclerEmpty.visibility = View.VISIBLE
            } else {
                binding.workoutsRecyclerEmpty.visibility = View.GONE
            }

            workoutAdapter.submitList(it)
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val workout = workoutAdapter.items[position]

                PopupDialog.getInstance(context)
                    .standardDialogBuilder()
                    .createStandardDialog()
                    .setHeading("Delete")
                    .setDescription(
                        "Are you sure to delete workout? This will also delete all associated sessions!" +
                                " This action cannot be undone"
                    )
                    .setIcon(R.drawable.ic_question)
                    .setIconColor(R.color.primary)
                    .setCancelable(false)
                    .setNegativeButtonCornerRadius(16F)
                    .setPositiveButtonCornerRadius(16F)
                    .setPositiveButtonBackgroundColor(R.color.danger)
                    .setPositiveButtonTextColor(R.color.white)
                    .setPositiveButtonText(getString(R.string.confirm))
                    .build(object : StandardDialogActionListener {
                        override fun onPositiveButtonClicked(dialog: Dialog) {
                            workoutViewModel.deleteWorkout(workout)
                            dialog.dismiss()

                            PopupDialog.getInstance(requireContext())
                                .statusDialogBuilder()
                                .createSuccessDialog()
                                .setHeading(getString(R.string.success))
                                .setDescription(getString(R.string.workout_deleted))
                                .setActionButtonText(getString(R.string.okay))
                                .build(Dialog::dismiss)
                                .show()
                        }

                        override fun onNegativeButtonClicked(dialog: Dialog) {
                            workoutAdapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                    })
                    .show()

            }
        }).attachToRecyclerView(binding.workoutsRecycler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutsFragment()
    }
}