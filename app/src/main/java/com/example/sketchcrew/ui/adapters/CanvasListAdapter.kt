package com.example.sketchcrew.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sketchcrew.R
import com.example.sketchcrew.data.local.models.CanvasData
import com.example.sketchcrew.ui.screens.CanvasListFragmentDirections

class CanvasListAdapter(
    private val canvasList: List<CanvasData>,
    private val itemClickListener: OnItemClickListener
    ) : RecyclerView.Adapter<CanvasListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onCanvasClick(canvasData: CanvasData)
        fun onSaveCanvas(canvasData: CanvasData)
        fun onDeleteCanvas(canvasData: CanvasData)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val del = itemView.findViewById<TextView>(R.id.textViewDelete)

        fun bindItems(canvasData: CanvasData) {
            val title = itemView.findViewById<TextView>(R.id.textViewTitle)
            title.text = "${canvasData.name}"
            val desc = itemView.findViewById<TextView>(R.id.textViewDesc)
            desc.text = "${canvasData.desc}"
            del.text = "Delete"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val listItems = LayoutInflater.from(parent.context).inflate(R.layout.canvas_list_item, parent, false)
        return ViewHolder(listItems)
    }

    override fun getItemCount(): Int {
        return canvasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = canvasList[position]
        holder.bindItems(canvasList[position])

        holder.itemView.setOnClickListener {
            itemClickListener.onCanvasClick(item)
        }
        holder.del.setOnClickListener {
            itemClickListener.onDeleteCanvas(item)
        }
    }

}
