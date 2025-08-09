package com.example.workmysets.fragments.onboarding

import android.app.Dialog
import android.graphics.Path.Op
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.workmysets.R
import com.example.workmysets.activities.OnboardingActivity
import com.example.workmysets.data.viewmodels.OnboardingViewModel
import com.example.workmysets.databinding.FragmentUserBinding
import com.example.workmysets.databinding.FragmentWelcomeBinding
import com.saadahmedev.popupdialog.PopupDialog


data class Option(val key: Char, val value: String) {
    override fun toString(): String = value
}

class UserFragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val genders = listOf("Man", "Woman")

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, genders
        )
        binding.gender.setAdapter(adapter)

        var gender = ""

        binding.gender.setOnItemClickListener { _, _, pos, _ ->
            gender = genders[pos]
            viewModel.gender.postValue(gender)
        }

        binding.prevButton.setOnClickListener { (activity as? OnboardingActivity)?.back() }

        binding.nextButton.setOnClickListener {

            val name = binding.name.text.toString()
            val age = binding.age.text.toString()

            val missingFields = mutableListOf<String>()

            if (name.isBlank()) {
                missingFields.add("Name")
            }
            if (age.isBlank()) {
                missingFields.add("Age")
            }
            if (gender.isBlank()) {
                missingFields.add("Gender")
            }

            if (missingFields.isNotEmpty()) {
                val fieldsString = missingFields.joinToString(", ")
                PopupDialog.getInstance(requireActivity()).statusDialogBuilder()
                    .createWarningDialog().setHeading(getString(R.string.oops))
                    .setDescription("Please fill the following fields: $fieldsString.")
                    .setActionButtonText(getString(R.string.okay)).build(Dialog::dismiss).show()
                return@setOnClickListener
            }

            viewModel.name.postValue(name)
            viewModel.age.postValue(age.toInt())

            (activity as? OnboardingActivity)?.next()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}