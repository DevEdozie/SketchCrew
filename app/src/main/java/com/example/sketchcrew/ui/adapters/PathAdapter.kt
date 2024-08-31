package com.example.sketchcrew.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.PathData

class PathAdapter(
    private val paths: List<PathData>,
    private val onItemClicked: (PathData) -> Unit
) : RecyclerView.Adapter<PathAdapter.PathViewHolder>() {

    inner class PathViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pathTextView = itemView.findViewById<TextView>(R.id.textViewTitle)
        val desc = itemView.findViewById<TextView>(R.id.textViewDesc)
//        private val pathTextView: TextView = itemView.findViewById(R.id.pathTextView)

        fun bind(pathData: PathData) {
            pathTextView.text = "Path ${pathData.name}"
            desc.text = "${pathData.desc}"
            itemView.setOnClickListener {
                onItemClicked(pathData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.canvas_list_item, parent, false)
        return PathViewHolder(view)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        holder.bind(paths[position])
    }

    override fun getItemCount() = paths.size
}
