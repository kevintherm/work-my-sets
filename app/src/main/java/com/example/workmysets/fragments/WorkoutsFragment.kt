package com.example.workmysets.fragments

import android.content.Intent
import android.graphics.Bitmap.Config
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.activities.ConfigureScheduleActivity
import com.example.workmysets.activities.CreateUpdateWorkoutActivity
import com.example.workmysets.adapters.WorkoutAdapter
import com.example.workmysets.data.models.ScheduleWithWorkouts
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentWorkoutsBinding
import com.example.workmysets.utils.Consts
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutsFragment : Fragment() {
    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var workoutViewModel: WorkoutViewModel

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

    }

    private fun setupScheduleSection() {
        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        binding.topBarScheduleSection.titleText.text = "Schedules"

        scheduleViewModel.scheduleWithWorkouts.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.scheduleProgress.progress = 0
                binding.scheduleProgressText.text = "0%"
                binding.scheduleNameText.text = "Your Schedule"
                binding.schedulePlanText.text = "Create a workout to configure your schedule..."
                binding.scheduleCard.isClickable = false
                binding.scheduleCard.isFocusable = false
            } else {
                binding.scheduleProgress.progress = 0
                binding.scheduleProgressText.text = "0%"
                binding.scheduleNameText.text = it.schedule.name

                val dayFormatter = DateTimeFormatter.ofPattern("EEEE")
                val day = LocalDate.now().format(dayFormatter)
                binding.schedulePlanText.text = "$day - Pull Day"

                binding.scheduleCard.isClickable = true
                binding.scheduleCard.isFocusable = true

                binding.scheduleCard.setOnClickListener{
                    val intent = Intent(requireContext(), ConfigureScheduleActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupWorkoutsSection() {
        binding.topBarWorkoutsSection.titleText.text = "Workouts"

        binding.topBarWorkoutsSection.actionButton.visibility = View.VISIBLE
        binding.topBarWorkoutsSection.actionButton.setImageResource(R.drawable.ic_add)
        binding.topBarWorkoutsSection.actionButton.setOnClickListener {
            val intent = Intent(context, CreateUpdateWorkoutActivity::class.java)
            startActivity(intent)
        }

        val workoutAdapter = WorkoutAdapter()
        binding.workoutsRecycler.layoutManager = LinearLayoutManager(context)
        binding.workoutsRecycler.adapter = workoutAdapter

        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class.java]

        workoutViewModel.allWorkouts.observe(viewLifecycleOwner) { workouts ->
            workoutAdapter.submitList(workouts)
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
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

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val intent =
                            Intent(context, CreateUpdateWorkoutActivity::class.java).apply {
                                putExtra(Consts.ARG_WORKOUT_ID, workout.workout.workoutId)
                            }
                        startActivity(intent)
                        workoutAdapter.notifyItemChanged(position) // Reset swipe
                    }

                    ItemTouchHelper.RIGHT -> {
                        workoutViewModel.deleteWorkout(workout)
                    }
                }
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