package com.example.campusconnect

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val description: String,
    val category: String, // e.g., "Academic", "Sports"
    var isAttending: Boolean = false // Tracks user interest
)