package ru.andrewkir.vtbmobile.Api

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.Observable
import org.json.JSONArray
import retrofit2.http.*
import ru.andrewkir.vtbmobile.DataClasses.*
import ru.andrewkir.vtbmobile.DataClasses.CarDetails


interface WebService {
    @POST("car_loan/")
    fun createNewCredit(
        @Body body: CreditRequest
    ): Observable<JsonElement>

    @POST("car_recognize/")
    fun recogniseCar(
        @Body body: CarPhoto
    ): Observable<JsonElement>

    @POST("car_recognize/")
    fun recogniseCar(
        @Header("Authorization") authHeader: String,
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

    @POST("accounts/token/refresh/")
    fun refreshToken(
        @Body body: JsonObject
    ): Observable<JWTResponse>

    @POST("marketplace_search/")
    fun marketPlaceSearch(
        @Body body: JsonObject
    ): Observable<CarDetails>

    @GET("search_history/")
    fun searchHistory(
        @Header("Authorization") authHeader: String
    ): Observable<JsonElement>
}

