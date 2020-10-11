package ru.andrewkir.vtbmobile.DataClasses

data class UserDataRequest (
    val email: String,
    val income_amount:Int,
    val birth_date_time:String,
    val birth_place:String,
    val family_name:String,
    val first_name:String,
    val gender:String,
    val middle_name:String,
    val nationality_country_code:String,
    val phone:String
)