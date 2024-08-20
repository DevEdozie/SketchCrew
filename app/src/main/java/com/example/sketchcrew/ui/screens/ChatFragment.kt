package com.example.sketchcrew.ui.screens

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sketchcrew.R
import com.example.sketchcrew.databinding.FragmentChatBinding
import com.example.sketchcrew.firebase.ChatMessage
import com.example.sketchcrew.firebase.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<ChatMessage>
    private lateinit var database: DatabaseReference
    private var currentUser: String = ""
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)


        // Get a reference to the database
        database =
            FirebaseDatabase.getInstance().getReference().child("messages")

        // Get a reference to the Firebase auth
        auth = Firebase.auth

        // Get a reference to the current user
        currentUser = auth.currentUser?.email.toString()

        // Initialize message
        messageList = ArrayList()


        setUpSendButton()
        setUpRecyclerView()


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpSendButton() {
        // Set up the send button click listener
        binding.sendButton.setOnClickListener {
            // Get current message in the message field
            val messageInput = binding.messageInputEditText.text.toString()

            // Get current date and time in a neat format
            val currentTime =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))


            if (messageInput.isNotEmpty()) {
                // Message Object
                val message = ChatMessage(
                    messageText = messageInput,
                    messageUser = currentUser,
                    messageTime = currentTime
                )
                // Add the message to the database
                val newMessageRef = database.push() // automatically generates a new key
                newMessageRef.setValue(message)
                // Clear the input field
                binding.messageInputEditText.text.clear()
            }

        }

        // Set up the database listener
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // Get the message value from the snapshot
                val message = snapshot.getValue(ChatMessage::class.java)

                // Add the message to the list
                if (message != null) {
                    messageList.add(message)
                    messageAdapter.notifyItemInserted(messageList.size - 1)
                    binding.messageRecyclerView.scrollToPosition(messageList.size - 1)
//                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setUpRecyclerView() {
        // Initialize the message adapter
        messageAdapter = MessageAdapter(requireContext(), messageList)
        binding.messageRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
            setHasFixedSize(true)
        }
    }


}