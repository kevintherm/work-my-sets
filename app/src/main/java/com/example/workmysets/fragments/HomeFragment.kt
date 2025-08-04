package com.example.workmysets.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.activities.SessionExerciseActivity
import com.example.workmysets.activities.WidgetsActivity
import com.example.workmysets.adapters.StreakWidgetManager
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentHomeBinding
import com.example.workmysets.utils.AppLocale
import com.example.workmysets.utils.Consts
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var sessionViewModel: SessionViewModel

    private lateinit var exerciseAdapter: ExerciseAdapter

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleViewModel = ViewModelProvider(requireActivity())[ScheduleViewModel::class]
        workoutViewModel = ViewModelProvider(requireActivity())[WorkoutViewModel::class]
        sessionViewModel = ViewModelProvider(requireActivity())[SessionViewModel::class]

        setupSetsWidget()
        setupHoursSpentWidget()

        exerciseAdapter = ExerciseAdapter()
        binding.dailySessionsRecycler.adapter = exerciseAdapter

        exerciseAdapter.onButtonPlayClick = this::gotoTrackExerciseActivity

        scheduleViewModel.scheduleWithWorkouts.observe(viewLifecycleOwner) { scheduleWithWorkouts ->
            val todayWorkout =
                scheduleWithWorkouts?.workouts?.find { LocalDate.now().dayOfWeek.value == it.dayOfWeek }

            if (todayWorkout == null) {
                binding.emptyMessage.visibility = View.VISIBLE
                binding.emptyMessage.text = "Hooray! No exercise for today!"
                return@observe
            } else {
                binding.emptyMessage.visibility = View.GONE
            }

            val workoutLiveData = workoutViewModel.findById(todayWorkout.workoutId)
            workoutLiveData.observeOnce(viewLifecycleOwner, Observer { foundWorkout ->
                if (foundWorkout == null) {
                    Toast.makeText(requireActivity(), "Invalid workout", Toast.LENGTH_SHORT).show()
                    return@Observer
                }

                sessionViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
                    val today = LocalDate.now()
                    val sessionToday = sessions.filter {
                        Instant.parse(it.session.startsAt)
                            .atZone(AppLocale.ZONE)
                            .toLocalDate() == today
                    }

                    val exercisedTodayIds = sessionToday.map { it.session.exerciseId }.toSet()
                    val dailyExercise =
                        foundWorkout.exercises.filter { it.exerciseId !in exercisedTodayIds }

                    exerciseAdapter.workoutId = foundWorkout.workout.workoutId
                    exerciseAdapter.updateList(dailyExercise)
                    if (dailyExercise.isEmpty()) {
                        binding.emptyMessage.visibility = View.VISIBLE
                        binding.emptyMessage.text = "You completed all sessions!"
                    } else {
                        binding.emptyMessage.visibility = View.GONE
                    }
                }

            })

        }

        val streakWidgetManager = StreakWidgetManager(
            requireContext(),
            binding.streakWidget
        )

        workoutViewModel.allWorkouts.observe(viewLifecycleOwner) { workouts ->
            sessionViewModel.allSessions.observeOnce(viewLifecycleOwner) { sessions ->
                streakWidgetManager.updateWidget(
                    workouts.map { it.workout },
                    sessions.map { it.session })
            }
        }

        binding.dailyStatsHeader.setOnClickListener {
            Intent(requireActivity(), WidgetsActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun setupHoursSpentWidget() {
        sessionViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            val zone = AppLocale.ZONE
            val today = LocalDate.now(zone)
            val filtered = sessions.sortedBy { it.session.startsAt }.filter {
                val startsAt = Instant.parse(it.session.startsAt)
                    .atZone(zone).toLocalDate()
                today == startsAt
            }

            val entries = filtered
                .mapIndexed { index, item ->
                    val start = Instant.parse(item.session.startsAt)
                    val end = Instant.parse(item.session.endsAt)

                    val duration = Duration.between(start, end)
                    val hours = duration.toMinutes().toDouble() / 60

                    Entry(index.toFloat() + 1, hours.toFloat())
                }

            binding.hoursSpentCount.text = String.format("%.3f", filtered.sumOf {
                val start = Instant.parse(it.session.startsAt)
                val end = Instant.parse(it.session.endsAt)

                val duration = Duration.between(start, end)

                duration.toMinutes().toDouble() / 60

            })

            // Create and style the dataset
            val dataSet = LineDataSet(entries, "Hours Spent")
            dataSet.color = requireActivity().getColor(R.color.accent2)// Orange line color
            dataSet.setDrawCircles(false) // No points
            dataSet.lineWidth = 4f // Line thickness
            dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER // Smooth, wavy curve
            dataSet.setDrawFilled(true) // Enable fill
            dataSet.setDrawCircles(false)
            dataSet.setDrawCircleHole(false)
            dataSet.setDrawValues(false)

            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    requireActivity().getColor(R.color.accent2Light),
                    Color.TRANSPARENT
                )
            )
            dataSet.fillDrawable = gradient

            // Create LineData
            val lineData = LineData(dataSet)

            // Configure the chart
            binding.hoursSpentChart.data = lineData
            binding.hoursSpentChart.description.isEnabled = false
            binding.hoursSpentChart.setTouchEnabled(false) // Disable touch for static look
            binding.hoursSpentChart.setPinchZoom(false)
            binding.hoursSpentChart.legend.isEnabled = false // Hide legend
            binding.hoursSpentChart.xAxis.setDrawGridLines(false)
            binding.hoursSpentChart.axisLeft.setDrawGridLines(false)
            binding.hoursSpentChart.axisRight.isEnabled = false
            binding.hoursSpentChart.axisLeft.axisMinimum = 0f
            binding.hoursSpentChart.xAxis.setDrawLabels(false)
            binding.hoursSpentChart.axisLeft.setDrawLabels(false)
            binding.hoursSpentChart.xAxis.setDrawAxisLine(false)
            binding.hoursSpentChart.axisLeft.setDrawAxisLine(false)

            binding.hoursSpentChart.setBackgroundColor(requireActivity().getColor(R.color.accent2Semi))
            binding.hoursSpentChart.extraBottomOffset = 30f
            binding.hoursSpentChart.minOffset = 0f
            binding.hoursSpentChart.extraTopOffset = 30f

            binding.hoursSpentChart.invalidate()
            binding.hoursSpentChart.animateY(1500, Easing.EaseInOutQuad)
        }
    }

    private fun setupSetsWidget() {
        sessionViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            val zone = AppLocale.ZONE
            val today = LocalDate.now(zone)
            val filtered = sessions.sortedBy { it.session.startsAt }.filter {
                val startsAt = Instant.parse(it.session.startsAt)
                    .atZone(zone).toLocalDate()
                today == startsAt
            }

            val entries = filtered
                .mapIndexed { index, item ->
                    Entry(
                        index.toFloat() + 1,
                        item.session.restsPerSet.size.coerceAtLeast(1).toFloat()
                    )
                }

            binding.setsWidgetCount.text = filtered.sumOf { it.session.repsPerSet.size }.toString()

            val dataSet = LineDataSet(entries, "Trend").apply {
                color = requireActivity().getColor(R.color.accent1)
                setDrawCircles(false)
                lineWidth = 4f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                setDrawValues(false)
                setDrawCircleHole(false)
                fillDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        requireActivity().getColor(R.color.accent1Light),
                        Color.TRANSPARENT
                    )
                )
            }

            binding.setsWidgetChart.apply {
                data = LineData(dataSet)
                description.isEnabled = false
                setTouchEnabled(false)
                setPinchZoom(false)
                legend.isEnabled = false
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                xAxis.setDrawLabels(false)
                axisLeft.setDrawLabels(false)
                xAxis.setDrawAxisLine(false)
                axisLeft.setDrawAxisLine(false)
                setBackgroundColor(requireActivity().getColor(R.color.accent1Semi))
                extraBottomOffset = 30f
                minOffset = 0f
                extraTopOffset = 30f
                invalidate()
                animateY(1000, Easing.EaseInOutQuad)
            }
        }
    }

    private fun gotoTrackExerciseActivity(exercise: Exercise) {

        val start = {
            val intent = Intent(requireActivity(), SessionExerciseActivity::class.java).apply {
                putExtra(Consts.ARG_EXERCISE_ID, exercise.exerciseId)
                putExtra(Consts.ARG_WORKOUT_ID, exerciseAdapter.workoutId)
            }
            startActivity(intent)
        }

        PopupDialog.getInstance(requireContext())
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
                    start()
                    dialog.dismiss()
                }

                override fun onNegativeButtonClicked(dialog: Dialog) {
                    dialog.dismiss()
                }
            })
            .show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

class ExerciseAdapter : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    var workoutId = -1L
    val items = mutableListOf<Exercise>()
    lateinit var onButtonPlayClick: (Exercise) -> Unit

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val exerciseName: TextView = view.findViewById(R.id.exerciseName)
        val quickStats: TextView = view.findViewById(R.id.quickStats)
        private val startExerciseButton: ImageButton = view.findViewById(R.id.startExerciseButton)

        fun bind(item: Exercise) {
            startExerciseButton.setOnClickListener {
                onButtonPlayClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_exercise_card_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.exerciseName.text = item.name
        holder.quickStats.text = "8/3 Sets ~ 3 min"

        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<Exercise>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}