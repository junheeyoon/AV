package com.av.ajouuniv.avproject2.api

import com.av.ajouuniv.avproject2.data.NetworkExample


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiInterface {
    @get:GET("/test")
    val user: Call<NetworkExample>

    @FormUrlEncoded
    @POST("/test")
    fun postUser(@Field("message") message: String): Call<NetworkExample>

    @FormUrlEncoded
    @PUT("/state")
    fun updateDevice(@Field("message") message: String): Call<NetworkExample>
}