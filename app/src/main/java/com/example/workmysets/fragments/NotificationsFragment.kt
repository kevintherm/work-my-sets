package com.example.workmysets.fragments

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.workmysets.R
import com.example.workmysets.databinding.FragmentNotificationsListDialogItemBinding
import com.example.workmysets.databinding.FragmentNotificationsListDialogBinding

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    NotificationsFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class NotificationsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNotificationsListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNotificationsListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dummyList = listOf(
            "New workout plan available",
            "Don't forget leg day tomorrow",
            "You’ve hit a new PR in squats!",
            "Weekly summary is ready",
            "Hydration reminder"
        )

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = ItemAdapter(dummyList)

        binding.dismissButton.setOnClickListener {
            dismiss()
        }
    }

    private inner class ViewHolder internal constructor(binding: FragmentNotificationsListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val text: TextView = binding.itemName
    }

    private inner class ItemAdapter(private val items: List<String>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                FragmentNotificationsListDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = items[position]
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): NotificationsFragment =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}