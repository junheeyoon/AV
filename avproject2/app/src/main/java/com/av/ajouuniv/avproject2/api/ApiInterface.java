package com.av.ajouuniv.avproject2.api;

import com.av.ajouuniv.avproject2.data.NetworkExample;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiInterface
{
    @GET("/test")
    Call<NetworkExample> getUser();

    @FormUrlEncoded
    @POST("/test")
    Call<NetworkExample> postUser(@Field("message") String message);

    @FormUrlEncoded
    @PUT("/state")
    Call<NetworkExample> updateDevice(@Field("message") String message);
}