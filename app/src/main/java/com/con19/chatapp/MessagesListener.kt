package com.con19.chatapp

interface MessagesListener {
    fun onMessageReceived(msg: MyMessage)
}