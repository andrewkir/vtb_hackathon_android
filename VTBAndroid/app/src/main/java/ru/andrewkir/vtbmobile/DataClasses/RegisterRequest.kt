package ru.andrewkir.vtbmobile.DataClasses

data class RegisterRequest(
    val username: String,
    val password: String,
    val password_confirm: String
)