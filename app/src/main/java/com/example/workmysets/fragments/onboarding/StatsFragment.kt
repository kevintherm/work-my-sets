package com.example.workmysets.fragments.onboarding

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.workmysets.R
import com.example.workmysets.activities.OnboardingActivity
import com.example.workmysets.data.viewmodels.OnboardingViewModel
import com.example.workmysets.databinding.FragmentStatsBinding
import com.example.workmysets.databinding.FragmentUserBinding
import com.google.android.material.textfield.TextInputEditText
import com.saadahmedev.popupdialog.PopupDialog

class StatsFragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prevButton.setOnClickListener { (activity as? OnboardingActivity)?.back() }

        binding.nextButton.setOnClickListener {

            val weight = binding.weight.text.toString()
            val height = binding.height.text.toString()

            if (weight.isBlank() || height.isBlank()) {
                PopupDialog.getInstance(requireActivity())
                    .statusDialogBuilder()
                    .createWarningDialog()
                    .setHeading(getString(R.string.oops))
                    .setDescription("Please fill the fields.")
                    .setActionButtonText(getString(R.string.okay))
                    .build(Dialog::dismiss)
                    .show()
                return@setOnClickListener
            }

            viewModel.weight.value = weight.toFloat()
            viewModel.height.value = height.toFloat()

            (activity as? OnboardingActivity)?.next()
        }
    }

    companion object {
        fun newInstance() = StatsFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}