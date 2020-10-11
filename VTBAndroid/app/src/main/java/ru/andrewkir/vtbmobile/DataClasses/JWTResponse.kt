package ru.andrewkir.vtbmobile.DataClasses

data class JWTResponse(
    val refresh: String,
    val access: String
)