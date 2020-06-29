package com.con19.chatapp

import android.util.Log
import java.io.DataInputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException


class ReceiveMessagesRunnable(private val messagesTask: MessagesTask): MessageRunnable {
    val TAG = this::class.simpleName
    private var thread: Thread? = null

    override fun getThread(): Thread? {
        return thread
    }

    override fun run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
        messagesTask.readMessagesThread = Thread.currentThread()
        thread = Thread.currentThread()
        try {
            val socket = Socket(IP_ADDRESS, SERVER_PORT_NUMBER)
            val inputStream = DataInputStream(socket.getInputStream())

            while (true) {
                val msg = inputStream.readUTF()
                messagesTask.message = msg
                messagesTask.handleDecodeState(DECODE_STATE_COMPLETED)
                Thread.sleep(200)
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, e.message ?: "Error")
        } catch (e: IOException) {
            Log.e(TAG, e.message ?: "Error")
        } catch (e: InterruptedException) {
            Log.e(TAG, e.message ?: "Error")
        }
        catch (e: Exception) {
            Log.e(TAG, e.message ?: "Error")
        }
    }

    companion object {
        const val DECODE_STATE_COMPLETED: Int = 1
    }
}