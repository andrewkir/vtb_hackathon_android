package ru.andrewkir.vtbmobile.DataClasses

data class CreditRequest(
    val comment: String,
    val customer_party: CustomerParty,
    val datetime: String,
    val interest_rate: Float,
    val requested_amount: Int,
    val requested_term: Int,
    val trade_mark: String,
    val vehicle_cost: Int

)

data class CustomerParty(
    val email: String,
    val income_amount: Int,
    val person: Person,
    val phone: String
)

data class Person(
    val birth_date_time: String,
    val birth_place: String,
    val family_name: String,
    val first_name: String,
    val middle_name: String,
    val gender: String,
    val nationality_country_code: String = "RU"
)