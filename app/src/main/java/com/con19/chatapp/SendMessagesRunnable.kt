package com.con19.chatapp

import android.util.Log
import com.google.gson.Gson
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

/**
 * Sends messages to a socket at the given IP address and port number.
 */
class SendMessagesRunnable : MessageRunnable {
    private val TAG = this::class.simpleName
    private val gson = Gson()
    private var thread: Thread? = null

    override fun getThread(): Thread? {
        return thread
    }
    override fun run() {
        try {
            val socket = Socket(IP_ADDRESS, SERVER_PORT_NUMBER)
            val outputStream = DataOutputStream(socket.getOutputStream())
            var jsonMessage: String
            var encryptedMessage: ByteArray
            var message: MyMessage
            while (true) {
                while (MessagesManager.outgoingMessages.isNotEmpty()) {
                    message = MessagesManager.outgoingMessages.remove()
                    jsonMessage = gson.toJson(
                        message
                    )
                    encryptedMessage = EncryptionService.encrypt(jsonMessage, message.recipientPublicKey)
                    outputStream.writeInt(encryptedMessage.size)
                    outputStream.write(encryptedMessage)
                }
                Thread.sleep(1000)
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, e.message ?: "Error")
        } catch (e: IOException) {
            Log.e(TAG, e.message ?: "Error")
        } catch (e: InterruptedException) {
            Log.e(TAG, e.message ?: "Error")
        }
    }
}