package com.shelldroid

data class TerminalLine(
    val command: String,
    val output: String,
    val timestamp: Long = System.currentTimeMillis()
)
