package com.ashlarprotocol.data

import kotlinx.serialization.Serializable

@Serializable
data class AarEntry(
    val id: String,
    val text: String,
    val timestamp: Long,
    val tags: List<String>
)
