package ru.andrewkir.vtbmobile.Api

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.andrewkir.vtbmobile.DataClasses.JWTResponse
import ru.andrewkir.vtbmobile.MainActivity


class ApiClient(val context: Context) {
    val instance: WebService by lazy {
        val okHttpClient = OkHttpClient.Builder().authenticator(CustomInterceptor(context)).build()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://vtb-backend.herokuapp.com/api/")
            .client(okHttpClient)
            .build()
        retrofit.create(WebService::class.java)
    }
}

class CustomInterceptor(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        // get a new token (I use a synchronous Retrofit call)
        val api = ApiClient(context).instance

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val refresh = sharedPref.getString("refresh_token", "")!!
        val json = JsonObject()
        json.addProperty("refresh", refresh)
        val access = api.refreshToken(json).blockingFirst().access

        // otherwise just pass the original response on
        return response.request().newBuilder()
            .header("Authorization", "Bearer $access")
            .build()
        ;
    }

}