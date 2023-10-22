package kp.ran.kitchenmanager

data class MessageFeature(
    val currentUserid: String,
    val cookUserId: String,
    val messageText: String,
    val timestamp: Long
)