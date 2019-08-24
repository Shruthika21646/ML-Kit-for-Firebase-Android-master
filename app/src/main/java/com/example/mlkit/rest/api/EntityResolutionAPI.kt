package com.example.mlkit.rest.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.SECONDS
import javax.xml.datatype.DatatypeConstants.MINUTES


/**
 *
 * Created by mohammed on 22/11/17.
 */

public class EntityResolutionAPI {

    interface RestAPIService {
        @Headers("Content-type: application/json")
        @POST("/api/get_entites_text")
        fun getEntitiesByText(@Body body: JsonObject): Call<ResponseBody>
    }

    companion object {
        private var okHttpClient = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
        private val retrofit = Retrofit.Builder()
                .baseUrl("https://wikify-service.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(okHttpClient)
                .build()

        var service = retrofit.create(RestAPIService::class.java)
    }
}
