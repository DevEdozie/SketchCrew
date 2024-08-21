package com.example.sketchcrew.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.data.local.models.Drawing
import com.example.sketchcrew.databinding.CanvasListItemBinding

// DrawingAdapter.kt
class DrawingAdapter(private val onClick: (Drawing) -> Unit) :
    ListAdapter<Drawing, DrawingAdapter.DrawingViewHolder>(DrawingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawingViewHolder {
        val binding = CanvasListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrawingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrawingViewHolder, position: Int) {
        val drawing = getItem(position)
        holder.bind(drawing)
        holder.itemView.setOnClickListener { onClick(drawing) }
    }

    class DrawingViewHolder(private val binding: CanvasListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drawing: Drawing) {
            // Bind data to views
            val textView: TextView = binding.textViewTitle
            textView.text = drawing.filename
        }
    }

    class DrawingDiffCallback : DiffUtil.ItemCallback<Drawing>() {
        override fun areItemsTheSame(oldItem: Drawing, newItem: Drawing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Drawing, newItem: Drawing): Boolean {
            return oldItem == newItem
        }
    }
}
