package com.con19.chatapp

import java.math.BigInteger

data class MyMessage (
    val sender: String,
    val content: String,
    val recipientPublicKey: BigInteger
)