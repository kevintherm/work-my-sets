package com.example.workmysets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workmysets.R
import com.example.workmysets.data.converter.Converters
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.session.entity.SessionWithExercise

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.ViewHolder>() {
    val items = mutableListOf<SessionWithExercise>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.exerciseName)
        val createdAt: TextView = view.findViewById(R.id.createdAt)
        val totalSets: TextView = view.findViewById(R.id.totalSets)
        val isCompleted: TextView = view.findViewById(R.id.isCompleted)
        val sideImage: ImageView = view.findViewById(R.id.sideImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_session_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.exerciseName.text = item.exercise.name
        holder.createdAt.text = Converters.diffForHumans(item.session.startsAt)
        holder.totalSets.text = item.session.repsPerSet.size.toString() + " Total sets"
        holder.isCompleted.text = if(item.session.isCompleted) "Completed" else "Abandoned"

        val imageIds = listOf(
            R.drawable.img_fitness_1,
            R.drawable.img_fitness_2,
            R.drawable.img_fitness_3
        )
        holder.sideImage.setImageResource(imageIds.random())
    }

    fun updateList(newList: List<SessionWithExercise>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}