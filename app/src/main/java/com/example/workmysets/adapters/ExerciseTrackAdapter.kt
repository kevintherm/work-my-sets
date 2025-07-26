package com.example.workmysets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.data.entities.exercise.entity.Exercise

class ExerciseTrackAdapter : RecyclerView.Adapter<ExerciseTrackAdapter.ViewHolder>() {
    val items = mutableListOf<Exercise>()
    lateinit var onButtonPlayClick: (Exercise) -> Unit

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val exerciseNameText: TextView = view.findViewById(R.id.exerciseNameText)
        val lastSetstext: TextView = view.findViewById(R.id.lastSetsText)
        val lastWeightsText: TextView = view.findViewById(R.id.lastWeightsText)
        val averageTimeText: TextView = view.findViewById(R.id.averageTimeText)
        val buttonStartExercise: ImageButton = view.findViewById(R.id.startExerciseButton)

        fun bind(item: Exercise) {
            buttonStartExercise.setOnClickListener {
                onButtonPlayClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_track_session_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val iconType =
            if (item.targetMuscle == "Cardio") R.drawable.ic_cycling else R.drawable.ic_dumbbell

        holder.exerciseNameText.text = item.name
        holder.exerciseNameText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, iconType, 0)

        holder.lastSetstext.text = "4"
        holder.lastWeightsText.text = "35kg"
        holder.averageTimeText.text = "4 min"

        holder.bind(item)
    }

    fun updateList(newList: List<Exercise>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}