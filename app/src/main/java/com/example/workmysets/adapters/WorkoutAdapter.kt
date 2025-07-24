package com.example.workmysets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.data.models.Workout
import com.example.workmysets.data.models.WorkoutWithExercises
import com.google.android.material.progressindicator.LinearProgressIndicator

class WorkoutAdapter: RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {
    val items = mutableListOf<WorkoutWithExercises>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutNameText: TextView = itemView.findViewById(R.id.workoutNameText)
        val masteryProgress: LinearProgressIndicator = itemView.findViewById(R.id.masteryProgress)
        val masteryProgressText: TextView = itemView.findViewById(R.id.masteryProgressText)
    }

    fun submitList(newList: List<WorkoutWithExercises>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val workoutMastery = if (item.exercises.isNotEmpty()) {
            0
        } else 0

        holder.workoutNameText.text = item.workout.name
        holder.masteryProgressText.text = "$workoutMastery%"
        holder.masteryProgress.progress = workoutMastery
    }

    override fun getItemCount(): Int = items.size

}