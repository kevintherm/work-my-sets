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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.activities.SessionExerciseActivity
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.FragmentHomeBinding
import com.example.workmysets.utils.Consts
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var workoutViewModel: WorkoutViewModel

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

        setupSetsWidget()
        setupHoursSpentWidget()

        exerciseAdapter = ExerciseAdapter()
        binding.dailySessionsRecycler.adapter = exerciseAdapter

        exerciseAdapter.onButtonPlayClick = this::gotoTrackExerciseActivity

        scheduleViewModel.scheduleWithWorkouts.observe(viewLifecycleOwner) { scheduleWithWorkouts ->
            val workoutSummary = scheduleWithWorkouts?.workouts?.find { LocalDate.now().dayOfWeek.value == it.dayOfWeek }

            if (workoutSummary == null) {
                binding.emptyMessage.visibility = View.VISIBLE
                return@observe
            } else {
                binding.emptyMessage.visibility = View.GONE
            }

            val workoutLiveData = workoutViewModel.findById(workoutSummary.workoutId)
            workoutLiveData.observe(viewLifecycleOwner) { foundWorkout ->
                if (foundWorkout == null) {
                    Toast.makeText(requireActivity(), "Invalid workout", Toast.LENGTH_SHORT).show()
                    return@observe
                }

                exerciseAdapter.workoutId = foundWorkout.workout.workoutId
                exerciseAdapter.updateList(foundWorkout.exercises)

                workoutLiveData.removeObservers(viewLifecycleOwner)
            }
        }

    }

    private fun setupHoursSpentWidget() {
        val entries = listOf(
            Entry(0f, 3f),
            Entry(1f, 5f),
            Entry(2f, 4f),
            Entry(3f, 6f),
            Entry(4f, 5.5f),
            Entry(5f, 6.2f),
        )

        // Create and style the dataset
        val dataSet = LineDataSet(entries, "Hours Spent")
        dataSet.color = requireActivity().getColor(R.color.accent2)// Orange line color
        dataSet.setDrawCircles(false) // No points
        dataSet.lineWidth = 4f // Line thickness
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth, wavy curve
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
        binding.hoursSpentChart.extraBottomOffset = 10f
        binding.hoursSpentChart.minOffset = 0f
        binding.hoursSpentChart.extraTopOffset = 30f

        binding.hoursSpentChart.invalidate()
        binding.hoursSpentChart.animateY(1500, Easing.EaseInOutQuad)
    }

    private fun setupSetsWidget() {
        val entries = listOf(
            Entry(0f, 3f),
            Entry(1f, 5f),
            Entry(2f, 4f),
            Entry(3f, 6f),
            Entry(4f, 5.5f),
            Entry(5f, 6.2f),
            Entry(6f, 5.8f),
            Entry(7f, 6.3f),
            Entry(7f, 3.3f),
            Entry(8f, 6.0f),
            Entry(9f, 5.0f),
            Entry(10f, 5.7f),
            Entry(11f, 7f)
        )

        // Create and style the dataset
        val dataSet = LineDataSet(entries, "Trend")
        dataSet.color = requireActivity().getColor(R.color.accent1)// Orange line color
        dataSet.setDrawCircles(false) // No points
        dataSet.lineWidth = 4f // Line thickness
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth, wavy curve
        dataSet.setDrawFilled(true) // Enable fill
        dataSet.setDrawCircles(false)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                requireActivity().getColor(R.color.accent1Light),
                Color.TRANSPARENT
            )
        )
        dataSet.fillDrawable = gradient

        // Create LineData
        val lineData = LineData(dataSet)

        // Configure the chart
        binding.setsWidgetChart.data = lineData
        binding.setsWidgetChart.description.isEnabled = false
        binding.setsWidgetChart.setTouchEnabled(false) // Disable touch for static look
        binding.setsWidgetChart.setPinchZoom(false)
        binding.setsWidgetChart.legend.isEnabled = false // Hide legend
        binding.setsWidgetChart.xAxis.setDrawGridLines(false)
        binding.setsWidgetChart.axisLeft.setDrawGridLines(false)
        binding.setsWidgetChart.axisRight.isEnabled = false
        binding.setsWidgetChart.axisLeft.axisMinimum = 0f
        binding.setsWidgetChart.xAxis.setDrawLabels(false)
        binding.setsWidgetChart.axisLeft.setDrawLabels(false)
        binding.setsWidgetChart.xAxis.setDrawAxisLine(false)
        binding.setsWidgetChart.axisLeft.setDrawAxisLine(false)

        binding.setsWidgetChart.setBackgroundColor(requireActivity().getColor(R.color.accent1Semi))
        binding.setsWidgetChart.extraBottomOffset = 10f
        binding.setsWidgetChart.minOffset = 0f
        binding.setsWidgetChart.extraTopOffset = 30f

        binding.setsWidgetChart.invalidate()
        binding.setsWidgetChart.animateY(1000, Easing.EaseInOutQuad)
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
        val startExerciseButton: ImageButton = view.findViewById(R.id.startExerciseButton)

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

    fun updateList(newList: List<Exercise>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

}