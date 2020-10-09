package ru.andrewkir.vtbmobile.Api

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.*


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

    @POST("links")
    fun createNewLink(@Header("AuthToken") authHeader: String,
                      @Header("accountNumber") accountHeader: String,
                      @Header("x-Idempotency-Key") idempotencyKey: String,
                      @Body body: JsonObject
    ): Observable<JsonObject>

    @GET("send")
    fun sendMoney(@Header("AuthToken") authHeader: String,
                  @Header("accountNumber") accountNumberHeader: String,
                  @Header("receiveAccountNumber") receiveAccountNumber: String,
                  @Header("sum") sum: Double, @Header("x-Idempotency-Key") idempotencyKey: String): Observable<JsonObject>
}