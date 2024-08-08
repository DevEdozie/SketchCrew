package com.example.sketchcrew

import Sketch
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.databinding.ItemSketchesBinding

class SketchAdapter(
    private val onDeleteClick: (Sketch) -> Unit
) : RecyclerView.Adapter<SketchAdapter.SketchViewHolder>() {

    private var sketches: List<Sketch> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SketchViewHolder {
        val binding = ItemSketchesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SketchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SketchViewHolder, position: Int) {
        val sketch = sketches[position]
        holder.bind(sketch)
    }

    override fun getItemCount(): Int = sketches.size

    fun submitList(newSketches: List<Sketch>) {
        sketches = newSketches
        notifyDataSetChanged()
    }

    inner class SketchViewHolder(private val binding: ItemSketchesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sketch: Sketch) {
            binding.title.text = sketch.title
            binding.iconDelete.setOnClickListener {
                onDeleteClick(sketch)
            }
        }
    }
}
