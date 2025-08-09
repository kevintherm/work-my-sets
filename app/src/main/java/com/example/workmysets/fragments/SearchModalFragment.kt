package com.example.workmysets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workmysets.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchModalFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_modal, container, false)
    }

    companion object {
        fun newInstance() = SearchModalFragment()
    }
}