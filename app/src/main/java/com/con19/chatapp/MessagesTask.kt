package com.con19.chatapp

class MessagesTask(val messagesListener: MessagesListener) {
    var readMessagesThread: Thread? = null
    var message: String? = null

    fun handleDecodeState(state: Int) {
        // convert decode state to overall state
        val outState = when (state) {
            ReceiveMessagesRunnable.DECODE_STATE_COMPLETED -> MessagesManager.TASK_COMPLETE
            else -> MessagesManager.TASK_INCOMPLETE
        }
        handleState(outState)
    }

    private fun handleState(state: Int) {
        MessagesManager.handleState(this, state)
    }

}