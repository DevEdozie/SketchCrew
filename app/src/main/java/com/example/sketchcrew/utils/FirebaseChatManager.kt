package com.example.sketchcrew.utils

import com.google.firebase.database.DatabaseReference

object FirebaseChatManager {
    var chatDrawingIdRef: DatabaseReference? = null
    var chatDB: DatabaseReference? = null
//     var typingStatusRef: DatabaseReference? = null

    // Initialize Firebase
    fun initializeChatDb(drawingIdRef: DatabaseReference){
        chatDrawingIdRef = drawingIdRef
        chatDB = drawingIdRef.child("messages")
//        typingStatusRef = chatDB?.child("typingStatus")
    }

}