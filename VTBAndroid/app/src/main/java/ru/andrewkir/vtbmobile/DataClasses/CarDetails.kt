package ru.andrewkir.vtbmobile.DataClasses

import com.google.gson.JsonArray

data class CarDetails(
    val make: String,
    val model: String,
    val types: JsonArray,
    val imageUrl: String,
    val minPrice: Int,
    val maxPrice: Int,
    val specs: Specs
)

data class Specs(
    val speedLimit: String,
    val acceleration: String,
    val fuelConsumption: String,
    val horsePowers: String
)