package com.example.sketchcrew.firebase

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sketchcrew.databinding.MessageItemBinding


class MessageAdapter(
    val context: Context,
    val messageList: ArrayList<ChatMessage>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // ViewHolder class to hold the view for each message item
    inner class MessageViewHolder(val itemBinding: MessageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messageList[position]

        holder.itemBinding.apply {
            messageUser.text = currentMessage.messageUser
            messageTime.text = currentMessage.messageTime.toString()
            messageText.text = currentMessage.messageText
        }
    }

    override fun getItemCount() = messageList.size


}



