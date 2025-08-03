package com.example.workmysets.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.activities.SessionDetailActivity
import com.example.workmysets.data.converter.Converters
import com.example.workmysets.data.entities.session.entity.SessionWithExercise
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.data.viewmodels.SessionsFragmentSharedViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentSessionsBinding
import com.example.workmysets.utils.Consts
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModels()
        setupRecyclerView()
        setupObservers()
        setupFilterButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeViewModels() {
        sessionViewModel = ViewModelProvider(requireActivity())[SessionViewModel::class.java]
        sharedViewModel =
            ViewModelProvider(requireActivity())[SessionsFragmentSharedViewModel::class.java]
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.topBar.titleText.text = "Sessions"
        sessionAdapter = SessionAdapter()
        binding.sessionsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sessionAdapter
        }

        sessionAdapter.onClickItem = { sessionWithExercise ->
            Intent(requireActivity(), SessionDetailActivity::class.java).apply {
                putExtra(Consts.ARG_SESSION_ID, sessionWithExercise.session.sessionId)
            }.also {
                startActivity(it)
            }
        }
    }

    private fun setupObservers() {
        sessionViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            sessionList = sessions
            applyFilters()
        }

        sharedViewModel.sort.observe(viewLifecycleOwner) { sort ->
            updateFilter("sort", sort)
        }

        sharedViewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            updateFilter("completed", completed)
        }

        sharedViewModel.filterDate.observe(viewLifecycleOwner) { date ->
            updateFilter("date", date)
        }

        sharedViewModel.workout.observe(viewLifecycleOwner) { workout ->
            updateFilter("workout", workout?.name)
        }
    }

    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            ItemListDialogFragment.newInstance()
                .show(requireActivity().supportFragmentManager, "FilterDialog")
        }
    }

    private fun updateFilter(key: String, value: String?) {
        if (!value.isNullOrBlank()) {
            activeFilters[key] = value
        } else {
            activeFilters.remove(key)
        }
        updateActiveFilters()
        applyFilters()
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

            filtered = when (daysBack) {
                -1 -> filtered
                0 -> filtered.filter { getSessionDate(it) == today }
                1 -> filtered.filter { getSessionDate(it) == today.minusDays(1) }
                else -> filtered.filter {
                    getSessionDate(it)?.isAfter(today.minusDays(daysBack.toLong())) ?: false
                }
            }
        }

        // Filter by workout
        sharedViewModel.workout.value?.let { workout ->
            filtered = filtered.filter { it.session.workoutId == workout.workoutId }
        }

        // Sort
        filtered = when (sharedViewModel.sort.value) {
            "Oldest" -> filtered.sortedBy { it.session.startsAt }
            "Newest" -> filtered.sortedByDescending { it.session.startsAt }
            else -> filtered
        }

        sessionAdapter.updateList(filtered)
    }

    private fun getSessionDate(session: SessionWithExercise): LocalDate? {
        return try {
            Instant.parse(session.session.startsAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (e: Exception) {
            null
        }
    }

    private fun updateActiveFilters() {
        binding.activeFiltersGroup.removeAllViews()
        activeFilters.forEach { (key, value) ->
            Chip(requireContext()).apply {
                text = "${key.replaceFirstChar { it.uppercase() }}: $value"
                isClickable = true
                isCheckable = false
                setChipBackgroundColorResource(R.color.bgLight)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                setOnClickListener {
                    activeFilters.remove(key)
                    sharedViewModel.clearParameter(key)
                    updateActiveFilters()
                    applyFilters()
                }
            }.also { binding.activeFiltersGroup.addView(it) }
        }
    }
}

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.ViewHolder>() {
    val items = mutableListOf<SessionWithExercise>()
    lateinit var onClickItem: (SessionWithExercise) -> Unit

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.exerciseName)
        val createdAt: TextView = view.findViewById(R.id.createdAt)
        val totalSets: TextView = view.findViewById(R.id.totalSets)
        val isCompleted: TextView = view.findViewById(R.id.isCompleted)
        val sideImage: ImageView = view.findViewById(R.id.sideImage)

        fun bind(item: SessionWithExercise) {
            view.setOnClickListener {
                onClickItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_session_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.exerciseName.text = item.exercise.name
        holder.createdAt.text = Converters.diffForHumans(item.session.startsAt)
        holder.totalSets.text = item.session.repsPerSet.size.toString() + " Total sets"
        holder.isCompleted.text = if (item.session.isCompleted) "Completed" else "Abandoned"

        val imageIds = listOf(
            R.drawable.img_fitness_1,
            R.drawable.img_fitness_2,
            R.drawable.img_fitness_3
        )
        holder.sideImage.setImageResource(imageIds.random())

        holder.bind(item)
    }

    fun updateList(newList: List<SessionWithExercise>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}