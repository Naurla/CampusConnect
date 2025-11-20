package com.example.campusconnect

data class RegisterRequest(
    val action: String = "register", // This tells PHP to run the register function
    val student_number: String,
    val fname: String,
    val mname: String,
    val lname: String,
    val gender: String,
    val dob: String,
    val email: String,
    val phone: String,
    val college: String,
    val program: String,
    val password: String
)
