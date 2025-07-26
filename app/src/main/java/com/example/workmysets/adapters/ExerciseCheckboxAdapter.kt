package com.example.workmysets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.data.entities.exercise.entity.Exercise

class ExerciseCheckboxAdapter(
    private val exercises: MutableList<Exercise>,
    private val selected: MutableSet<Exercise>
) : RecyclerView.Adapter<ExerciseCheckboxAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.exerciseCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_checkbox, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.checkBox.text = exercise.name
        holder.checkBox.setOnCheckedChangeListener(null) // prevent callback reuse
        holder.checkBox.isChecked = selected.contains(exercise)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selected.add(exercise)
            } else {
                selected.remove(exercise)
            }
        }
    }

    fun updateData(newList: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = exercises.size
    fun getExerciseAt(position: Int): Exercise {
        return exercises[position]
    }
}
