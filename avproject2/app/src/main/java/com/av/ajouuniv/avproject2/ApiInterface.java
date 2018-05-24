package com.av.ajouuniv.avproject2;

import com.av.ajouuniv.avproject2.data.NetworkExample;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiInterface
{
    @GET("/test")
    Call<NetworkExample> getUser();

    @POST("/test")
    Call<NetworkExample> postUser();

    @PUT("/test")
    Call<NetworkExample> updateUser();
}