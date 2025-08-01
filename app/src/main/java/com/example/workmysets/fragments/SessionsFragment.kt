package com.example.workmysets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workmysets.R
import com.example.workmysets.adapters.SessionAdapter
import com.example.workmysets.data.entities.session.entity.SessionWithExercise
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.data.viewmodels.SessionsFragmentSharedViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentSessionsBinding
import com.google.android.material.chip.Chip
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SessionsFragment : Fragment() {
    private var _binding: FragmentSessionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var sharedViewModel: SessionsFragmentSharedViewModel

    private lateinit var sessionList: List<SessionWithExercise>
    private val activeFilters = mutableMapOf<String, String>()

    companion object {
        fun newInstance() = SessionsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionViewModel = ViewModelProvider(requireActivity())[SessionViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SessionsFragmentSharedViewModel::class.java]
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class.java]

        binding.topBar.titleText.text = "Sessions"

        sessionAdapter = SessionAdapter()
        binding.sessionsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.sessionsRecycler.adapter = sessionAdapter

        sessionViewModel.allSessions.observe(viewLifecycleOwner) { allSessions ->
            sessionList = allSessions
            applyFilters()
        }

        sharedViewModel.sort.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                activeFilters["sort"] = it
            } else {
                activeFilters.remove("sort")
            }
            updateActiveFilters()
            applyFilters()
        }

        sharedViewModel.isCompleted.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                activeFilters["completed"] = it
            } else {
                activeFilters.remove("completed")
            }
            updateActiveFilters()
            applyFilters()
        }

        sharedViewModel.filterDate.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                activeFilters["date"] = it
            } else {
                activeFilters.remove("date")
            }
            updateActiveFilters()
            applyFilters()
        }

        sharedViewModel.workout.observe(viewLifecycleOwner) { workout ->
            if (workout != null) {
                activeFilters["workout"] = workout.name
            } else {
                activeFilters.remove("workout")
            }
            updateActiveFilters()
            applyFilters()
        }

        binding.filterButton.setOnClickListener {
            val dialog = ItemListDialogFragment.newInstance()
            dialog.show(requireActivity().supportFragmentManager, "FilterDialog")
        }
    }

    private fun applyFilters() {
        var filtered = sessionList

        // Filter by completion status
        when (sharedViewModel.isCompleted.value) {
            "Yes" -> filtered = filtered.filter { it.session.isCompleted }
            "No" -> filtered = filtered.filter { !it.session.isCompleted }
        }

        // Filter by date range
        sharedViewModel.filterDate.value?.let { label ->
            val index = sharedViewModel.dateLabels.indexOf(label)
            val daysBack = sharedViewModel.dateValues.getOrNull(index) ?: -1
            val today = LocalDate.now()

            fun sessionDate(session: SessionWithExercise): LocalDate? {
                return try {
                    Instant.parse(session.session.startsAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                } catch (e: Exception) {
                    null
                }
            }

            filtered = when (daysBack) {
                -1 -> filtered
                0 -> filtered.filter { sessionDate(it) == today }
                1 -> filtered.filter { sessionDate(it) == today.minusDays(1) }
                else -> {
                    val cutoff = today.minusDays(daysBack.toLong())
                    filtered.filter {
                        val date = sessionDate(it)
                        date != null && !date.isBefore(cutoff)
                    }
                }
            }
        }

        // Filter by workout
        sharedViewModel.workout.value?.let { workout ->
            filtered = filtered.filter { it.session.workoutId == workout.workoutId }
        }

        // Sort
        when (sharedViewModel.sort.value) {
            "Oldest" -> filtered = filtered.sortedBy { it.session.startsAt }
            "Newest" -> filtered = filtered.sortedByDescending { it.session.startsAt }
        }

        sessionAdapter.updateList(filtered)
    }

    private fun updateActiveFilters() {
        binding.activeFiltersGroup.removeAllViews()
        for ((key, value) in activeFilters) {
            val chip = Chip(requireContext())
            chip.text = "${key.replaceFirstChar { it.uppercase() }}: $value"
            chip.isClickable = true
            chip.isCheckable = false
            chip.setChipBackgroundColorResource(R.color.bgLight)
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))

            chip.setOnClickListener {
                activeFilters.remove(key)
                sharedViewModel.clearParameter(key)
                updateActiveFilters()
                applyFilters()
            }

            binding.activeFiltersGroup.addView(chip)
        }
    }
}
