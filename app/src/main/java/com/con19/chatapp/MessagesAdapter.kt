package com.con19.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.outgoing_chat_bubble.view.*

class MessagesAdapter(private val messages: MutableList<MyMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MessagesViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.chatBubbleTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewId = when (viewType) {
            INCOMING -> R.layout.incoming_chat_bubble
            OUTGOING -> R.layout.outgoing_chat_bubble
            else -> R.layout.outgoing_chat_bubble

        }
        val view = LayoutInflater.from(parent.context)
            .inflate(viewId, parent, false)
        return MessagesViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].sender) {
            USER_ID -> OUTGOING
            else -> INCOMING
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessagesViewHolder).textView.text = messages[position].content
    }
}
