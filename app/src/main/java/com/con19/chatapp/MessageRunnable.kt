package com.con19.chatapp

interface MessageRunnable: Runnable {
    fun getThread(): Thread?
}