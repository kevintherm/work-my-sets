package com.example.workmysets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workmysets.R
import com.example.workmysets.adapters.SessionAdapter
import com.example.workmysets.data.entities.schedule.entity.Schedule
import com.example.workmysets.data.viewmodels.ScheduleViewModel
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.databinding.FragmentHomeBinding
import com.example.workmysets.databinding.FragmentSessionsBinding

class SessionsFragment : Fragment() {
    private var _binding: FragmentSessionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var sessionViewModel: SessionViewModel

    companion object {
        fun newInstance() = SessionsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSessionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class]

        binding.topBar.titleText.text = "Sessions"

        sessionAdapter = SessionAdapter()

        sessionViewModel.allSessions.observe(viewLifecycleOwner) { allSessionsWithExercise ->
            sessionAdapter.updateList(allSessionsWithExercise)
        }

        binding.sessionsRecycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.sessionsRecycler.adapter = sessionAdapter
    }
}