package com.example.workmysets.fragments.onboarding

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.workmysets.R
import com.example.workmysets.activities.OnboardingActivity
import com.example.workmysets.data.entities.user.entity.User
import com.example.workmysets.data.viewmodels.OnboardingViewModel
import com.example.workmysets.data.viewmodels.UserViewModel
import com.example.workmysets.databinding.FragmentFinishBinding
import com.example.workmysets.databinding.FragmentStatsBinding
import com.saadahmedev.popupdialog.PopupDialog

class FinishFragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prevButton.setOnClickListener { (activity as? OnboardingActivity)?.back() }

        var name = viewModel.name.value
        var age = viewModel.age.value
        var gender = viewModel.gender.value
        var weight = viewModel.weight.value
        var height = viewModel.height.value

        binding.nextButton.setOnClickListener {
            val user = User(
                name = name,
                age = age,
                gender = gender,
                weight = weight,
                height = height,
                profilePath = null
            )

            userViewModel.insert(user)

            (activity as? OnboardingActivity)?.next()
        }

    }

    companion object {
        fun newInstance() = FinishFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}