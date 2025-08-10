package com.example.workmysets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workmysets.R
import com.example.workmysets.databinding.FragmentFilterBottomSheetBinding
import com.example.workmysets.databinding.FragmentSearchModalBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchModalFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSearchModalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchModalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = SearchModalFragment()
    }
}