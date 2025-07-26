package com.example.workmysets.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.util.findColumnIndexBySuffix
import com.example.workmysets.R
import com.example.workmysets.activities.TrackSessionActivity
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.databinding.FragmentHomeBinding
import java.time.LocalDate

class HomeFragment : Fragment(), OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleViewModel: ScheduleViewModel
//    private lateinit var sessionViewModel

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

        populateWidgets()

        binding.sessionProgressWidget.setOnClickListener(this)
    }

    private fun populateWidgets() {
        val now = LocalDate.now()



        // Schedule Progress Widget
        val weeklyProgress = ((now.dayOfWeek.value.toDouble() / 7) * 100).toInt()
        binding.scheduleProgress.progress = weeklyProgress
        binding.scheduleProgressText.text = "$weeklyProgress%"
        binding.scheduleProgressSubtext.text = "${100 - weeklyProgress}% left to go!"

        // Schedule Stats
        scheduleViewModel.scheduleWithWorkouts.observe(requireActivity()) { scheduleWithWorkouts ->
            val currentWorkout = scheduleWithWorkouts.workouts // today's workout
                .filter { it.dayOfWeek == now.dayOfWeek.value }

            val currentSchedule = currentWorkout.joinToString(", ") { it.name }

            val text = if (currentWorkout.isNotEmpty()) currentSchedule else "Rest Day"
            binding.currentScheduleText.text = text

            var icon = R.drawable.ic_dumbbell_lg
            var tint = R.color.accent2

            if (currentWorkout.isEmpty()) {
                icon = R.drawable.ic_sleep_lg
                tint = R.color.secondary
            } else if (listOf("leg", "lower").any {
                    currentWorkout[0]?.name?.contains(
                        it,
                        true
                    ) == true
                }) {
                icon = R.drawable.ic_skull_lg
                tint = R.color.danger
            }

            val drawable = ContextCompat.getDrawable(requireContext(), icon)
            drawable?.setTint(ContextCompat.getColor(requireContext(), tint))
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            binding.currentScheduleText.setTextColor(requireActivity().getColor(tint))
            binding.currentScheduleText.setCompoundDrawables(
                null, drawable, null, null
            )

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(button: View?) = when (button?.id) {
        R.id.sessionProgressWidget -> onClickSessionWidget()
        else -> Unit
    }

    private fun onClickSessionWidget() {
        val intent = Intent(requireContext(), TrackSessionActivity::class.java)
        startActivity(intent)
    }

}