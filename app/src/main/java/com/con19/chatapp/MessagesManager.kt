package com.con19.chatapp

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object MessagesManager {
    private val TAG = this::class.simpleName
    val outgoingMessages: Queue<MyMessage> = LinkedList()
    val gson = Gson()

    private val messagesWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private const val KEEP_ALIVE_TIME = 1L
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    private val threadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        messagesWorkQueue
    )

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(inputMessage: Message) {
            val messageTask = inputMessage.obj as MessagesTask

            when (inputMessage.what) {
                TASK_COMPLETE -> {
                    messageTask.message?.let {
                        parseAndReceiveMessage(it, messageTask.messagesListener)
                    }
                }
                else -> super.handleMessage(inputMessage)
            }
        }

        private fun parseAndReceiveMessage(
            it: String,
            messagesListener: MessagesListener
        ) {
            try {
                val msg = gson.fromJson(it, MyMessage::class.java)
                // Server will broadcast our own messages back to us, which we can safely ignore.
                // In future, may be worth looking at having confirmation of message sent using this.
                if (msg.sender != USER_ID) {
                    messagesListener.onMessageReceived(msg)
                }
            } catch (ex: JsonSyntaxException) {
                Log.e(TAG, "Error parsing message $")
            }
        }
    }

    fun startReceivingMessages(runnable: ReceiveMessagesRunnable) {
        threadPool.execute(runnable)
    }

    fun sendMessage(runnable: SendMessagesRunnable, msg: MyMessage) {
        outgoingMessages.offer(msg)
        threadPool.execute(runnable)
    }

    /**
     * Create a message for the handler with the state and task object
     */
    fun handleState(messagesTask: MessagesTask, state: Int) {
        handler.obtainMessage(state, messagesTask).apply {
            sendToTarget()
        }
    }

    fun cancelAll() {
        /*
         * Creates and populates an array of Runnables with the Runnables in the queue
         */
        val runnableArray: Array<Runnable> = messagesWorkQueue.toTypedArray()
        /*
         * Iterates over the array of Runnables and interrupts each one's Thread.
         */
        synchronized(this) {
            // Iterates over the array of tasks
            runnableArray.map { (it as? MessageRunnable)?.getThread() }
                .forEach { thread ->
                    thread?.interrupt()
                }
        }
    }

    const val TASK_INCOMPLETE = 2
    const val TASK_COMPLETE = 1
}
