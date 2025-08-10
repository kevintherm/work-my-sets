package com.example.workmysets.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.activities.WidgetsActivity
import com.example.workmysets.databinding.FragmentFilterBottomSheetBinding
import com.example.workmysets.databinding.FragmentSearchModalBinding
import com.example.workmysets.ui.components.SearchItem
import com.example.workmysets.utils.Consts
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

        val searchItems = Consts.getShortcutsSearchList()

        val notFoundSearchItem = listOf(SearchItem("Item not found.", null))

        val searchAdapter = SearchRecyclerAdapter()

        binding.searchRecycler.adapter = searchAdapter
        binding.searchRecycler.layoutManager = LinearLayoutManager(requireActivity())

        binding.dismissButton.setOnClickListener {
            dismiss()
        }

        binding.clearInput.setOnClickListener {
            binding.searchInput.setText("")
        }

        binding.searchInput.addTextChangedListener { text: Editable? ->
            val query = text?.toString().orEmpty()

            binding.clearInput.visibility = if (query.isBlank()) View.GONE else View.VISIBLE

            val result = searchItems.filter {
                it.name.contains(query, ignoreCase = true)
            }

            if (result.isEmpty()) {
                searchAdapter.updateList(notFoundSearchItem)
            } else {
                searchAdapter.updateList(result)
            }
        }

        // enter if the reuslt is only 1
        binding.searchInput.setOnEditorActionListener { _, actionId, event ->
            val isEnterPressed = actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)

            if (isEnterPressed) {
                val query = binding.searchInput.text.toString()
                val result = searchItems.filter {
                    it.name.contains(query, ignoreCase = true)
                }

                if (result.size == 1) {

                    result.first().action?.invoke(requireContext())

                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
                    binding.searchInput.clearFocus()
                }
                true
            } else {
                false
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = SearchModalFragment()
    }
}

class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
    private val items = mutableListOf<SearchItem>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val card: CardView = view.findViewById(R.id.card)

        fun bind(item: SearchItem) {
            if (item.action == null) {
                card.isClickable = false
                card.isFocusable = false
            } else {
                card.isClickable = true
                card.isFocusable = true
                item.action?.invoke(itemView.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemName.text = item.name

        holder.card.setOnClickListener {
            holder.bind(item)
        }
    }

    fun updateList(newList: List<SearchItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

}