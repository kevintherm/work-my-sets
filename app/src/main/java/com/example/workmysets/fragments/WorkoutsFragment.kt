package com.example.workmysets.fragments

import android.content.Intent
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
import com.example.workmysets.activities.CreateUpdateWorkoutActivity
import com.example.workmysets.adapters.WorkoutAdapter
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentWorkoutsBinding
import com.example.workmysets.utils.Consts

class WorkoutsFragment : Fragment() {
    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutViewModel: WorkoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topBar.titleText.text = "Workout List"

        binding.topBar.actionButton.visibility = View.VISIBLE
        binding.topBar.actionButton.setImageResource(R.drawable.ic_add)
        binding.topBar.actionButton.setOnClickListener{
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

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
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
                        val intent = Intent(context, CreateUpdateWorkoutActivity::class.java).apply {
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