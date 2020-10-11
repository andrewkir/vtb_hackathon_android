package ru.andrewkir.vtbmobile.Api

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.reactivex.Observable
import retrofit2.http.*
import ru.andrewkir.vtbmobile.DataClasses.*


interface WebService {
    @GET("links")
    fun getAllLinks(
        @Header("AuthToken") authHeader: String,
        @Header("accountNumber") accountHeader: String
    ): Observable<JsonObject>

    @GET("links/{linkId}")
    fun getLinkInfo(
        @Header("AuthToken") authHeader: String,
        @Path(
            value = "linkId",
            encoded = true
        ) linkId: String
    ): Observable<JsonObject>

    @GET("balance")
    fun getBalance(
        @Header("AuthToken") authHeader: String,
        @Header("accountNumber") accountHeader: String
    ): Observable<JsonObject>

    @POST("car_loan/")
    fun createNewCredit(
        @Body body: CreditRequest
    ): Observable<JsonElement>

    @POST("car_recognize/")
    fun recogniseCar(
        @Body body: CarPhoto
    ): Observable<JsonElement>

    @POST("accounts/token/")
    fun login(
        @Body body: LoginRequest
    ): Observable<JWTResponse>

    @POST("accounts/register/")
    fun register(
        @Body body: RegisterRequest
    ): Observable<JsonElement>

    @GET("extra_user_data/")
    fun extraData(
        @Header("Authorization") authHeader: String
    ): Observable<JsonElement>

    @PATCH("extra_user_data/{id}/")
    fun saveExtraData(
        @Path(value = "id", encoded = true) id: Int,
        @Header("Authorization") authHeader: String,
        @Body body: UserDataRequest
    ): Observable<JsonElement>

    @POST("extra_user_data/")
    fun saveExtraDataPOST(
        @Header("Authorization") authHeader: String,
        @Body body: JsonElement
    ): Observable<JsonElement>

    @GET("send")
    fun sendMoney(
        @Header("AuthToken") authHeader: String,
        @Header("accountNumber") accountNumberHeader: String,
        @Header("receiveAccountNumber") receiveAccountNumber: String,
        @Header("sum") sum: Double, @Header("x-Idempotency-Key") idempotencyKey: String
    ): Observable<JsonObject>
}

