package ru.andrewkir.vtbmobile.Api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val instance: WebService by lazy{
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://vtb-backend.herokuapp.com/api/")
            .build()
        retrofit.create(WebService::class.java)
    }
}