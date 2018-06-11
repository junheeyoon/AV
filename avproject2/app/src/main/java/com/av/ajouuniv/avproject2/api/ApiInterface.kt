package com.av.ajouuniv.avproject2.api

import com.av.ajouuniv.avproject2.data.NetworkExample
import com.av.ajouuniv.avproject2.data.NetworkExample1
import com.av.ajouuniv.avproject2.data.NetworkExample2


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE

interface ApiInterface {
    @get:GET("/test")
    val user: Call<NetworkExample>

    @FormUrlEncoded
    @POST("/test")
    fun postUser(@Field("message") message: String): Call<NetworkExample>

    @FormUrlEncoded
    @PUT("/state")
    fun updateDevice(@Field("message") message: String): Call<NetworkExample>

    @FormUrlEncoded
    @DELETE("/device")
    fun deleteDevice(@Field("message") message: String): Call<NetworkExample1>

    @FormUrlEncoded
    @POST("/device")
    fun postDevice(@Field("message") message: String): Call<NetworkExample2>
}