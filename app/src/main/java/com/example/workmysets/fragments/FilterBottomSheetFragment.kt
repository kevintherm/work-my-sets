package com.example.workmysets.fragments

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.example.workmysets.data.entities.workout.entity.Workout
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.viewmodels.SessionsFragmentSharedViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentFilterBottomSheetBinding

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterBottomSheetBinding? = null

    private lateinit var sharedViewModel: SessionsFragmentSharedViewModel
    private lateinit var workoutViewModel: WorkoutViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedViewModel =
            ViewModelProvider(requireActivity())[SessionsFragmentSharedViewModel::class]
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class]

        binding.dismissButton.setOnClickListener {
            dismiss()
        }

        val sortOptions = listOf("Latest", "Oldest")
        val sortAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sortOptions)

        binding.sortDropdown.setAdapter(sortAdapter)
        binding.sortDropdown.setOnItemClickListener({ parent, view, position, id ->
            val selectedItem = sortOptions[position]
            sharedViewModel.sort.postValue(selectedItem)
        })
        sharedViewModel.sort.observe(viewLifecycleOwner) {
            binding.sortDropdown.setText(it, false)
        }

        val isCompletedOptions = listOf("All", "Yes", "No")
        val isCompletedAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            isCompletedOptions
        )

        binding.typeDropdown.setAdapter(isCompletedAdapter)
        binding.typeDropdown.setOnItemClickListener({ parent, view, position, id ->
            val selectedItem = isCompletedOptions[position]
            sharedViewModel.isCompleted.postValue(selectedItem)
        })
        sharedViewModel.isCompleted.observe(viewLifecycleOwner) {
            binding.typeDropdown.setText(it, false)
        }


        val dateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            sharedViewModel.dateLabels
        )

        binding.dateDropdown.setAdapter(dateAdapter)
        binding.dateDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = sharedViewModel.dateLabels[position]
            sharedViewModel.filterDate.postValue(selectedItem)
        }
        sharedViewModel.filterDate.observe(viewLifecycleOwner) {
            binding.dateDropdown.setText(it, false)
        }

        workoutViewModel.allWorkouts.observe(viewLifecycleOwner) {
            val workoutNames = mutableListOf<String>()
            workoutNames.clear()
            workoutNames.add("All")
            workoutNames.addAll(it.map { it.workout.name })

            val workoutAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                workoutNames
            )
            binding.workoutDropdown.setAdapter(workoutAdapter)
            binding.workoutDropdown.setOnItemClickListener{ _, _, position, _ ->
                val selectedItem = it.find { it.workout.name == workoutNames[position] }
                sharedViewModel.workout.postValue(selectedItem?.workout)
            }
        }
        sharedViewModel.workout.observe(viewLifecycleOwner) {
            if (it != null) binding.workoutDropdown.setText(it.name, false)
        }
    }

    companion object {
        fun newInstance(): ItemListDialogFragment = ItemListDialogFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}