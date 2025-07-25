package com.example.workmysets.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.data.models.Workout
import com.example.workmysets.ui.objects.Day

class WorkoutConfigureAdapter: RecyclerView.Adapter<WorkoutConfigureAdapter.ViewHolder>() {
    val items = mutableListOf<Workout>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemsDropdown = Day.getWeekDays()

        val workoutNameText: TextView = itemView.findViewById(R.id.workoutNameText)
        val selectDropdown: AutoCompleteTextView = itemView.findViewById(R.id.selectDaysDropdown)
        val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_dropdown_item_1line, itemsDropdown)
    }

    fun submitList(newList: List<Workout>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutConfigureAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_configure, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutConfigureAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.workoutNameText.text = item.name
        holder.selectDropdown.setAdapter(holder.adapter)
        val selectedValue = holder.itemsDropdown.find { it.key == item.dayOfWeek }?.label ?: ""
        holder.selectDropdown.setText(selectedValue, false)
        holder.selectDropdown.setOnItemClickListener({ parent, view, position, id ->
            val selectedItem = holder.itemsDropdown[position]
            item.dayOfWeek = selectedItem.key
        })
    }

    override fun getItemCount(): Int = items.size

}