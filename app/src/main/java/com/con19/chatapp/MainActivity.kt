package com.con19.chatapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


const val INCOMING = 1
const val OUTGOING = 2

class MainActivity : AppCompatActivity(), MessagesListener {
    private val parser = Gson()
    private val messages: MutableList<MyMessage> = mutableListOf()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setKeys()
        initRecyclerView()

        val receiveMessagesRunnable = ReceiveMessagesRunnable(
            MessagesTask(this)
        )

        MessagesManager.startReceivingMessages(receiveMessagesRunnable)

        sendButton.setOnClickListener {
            val msg = MyMessage(USER_ID, sendMessageEditText.text.toString())
            sendMessageEditText.setText("")
            messages.add(msg)
            viewAdapter.notifyDataSetChanged()
            MessagesManager.sendMessage(
                SendMessagesRunnable(),
                msg
            )
        }
    }

    /**
     * If the app has not set up encryption keys, then set up new encryption keys and
     * store as json objects in sharedPreferences.
     * Otherwise does nothing.
     */
    private fun setKeys() {
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)

        if (!sharedPreferences.contains(getString(R.string.private_key))) {
            val keys: Pair<PrivateKey, PublicKey> = EncryptionService.generateKeys()
            val privateKey = keys.first
            val publicKey = keys.second

            with(sharedPreferences.edit()) {
                putString(
                    getString(R.string.private_key),
                    parser.toJson(privateKey)
                )
                putString(
                    getString(R.string.public_key),
                    parser.toJson(publicKey)
                )
                commit()
            }
        }
    }

    private fun initRecyclerView() {
        val viewManager = LinearLayoutManager(this)
        viewAdapter = MessagesAdapter(messages)
        messages_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onMessageReceived(msg: MyMessage) {
        messages.add(msg)
        viewAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        // Terminate all message read/write threads
        MessagesManager.cancelAll()
        super.onDestroy()
    }
}
