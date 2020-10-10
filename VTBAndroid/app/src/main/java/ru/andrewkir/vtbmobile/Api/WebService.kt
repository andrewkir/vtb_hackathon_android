package ru.andrewkir.vtbmobile.Api

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.reactivex.Observable
import retrofit2.http.*
import ru.andrewkir.vtbmobile.DataClasses.CarPhoto
import ru.andrewkir.vtbmobile.DataClasses.CreditRequest


interface WebService {
    @GET("links")
    fun getAllLinks(@Header("AuthToken") authHeader: String,
                    @Header("accountNumber") accountHeader: String): Observable<JsonObject>

    @GET("links/{linkId}")
    fun getLinkInfo(
        @Header("AuthToken") authHeader: String,
        @Path(
            value = "linkId",
            encoded = true
        ) linkId: String
    ): Observable<JsonObject>

    @GET("balance")
    fun getBalance(@Header("AuthToken") authHeader: String,
                   @Header("accountNumber") accountHeader: String): Observable<JsonObject>

    @POST("car_loan/")
    fun createNewCredit(@Body body: CreditRequest
    ): Observable<JsonElement>

    @POST("car_recognize/")
    fun recogniseCar(@Body body: CarPhoto
    ): Observable<JsonElement>

    @GET("send")
    fun sendMoney(@Header("AuthToken") authHeader: String,
                  @Header("accountNumber") accountNumberHeader: String,
                  @Header("receiveAccountNumber") receiveAccountNumber: String,
                  @Header("sum") sum: Double, @Header("x-Idempotency-Key") idempotencyKey: String): Observable<JsonObject>
}