package com.av.ajouuniv.avproject2;

import com.av.ajouuniv.avproject2.data.UserDetails;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

public interface ApiInterface
{
    @GET("user")
    Call<UserDetails> getUser(@QueryMap Map<String, String> params);

    @POST("user")
    Call<UserDetails> postUser(@QueryMap Map<String, String> params);

    @PUT("user")
    Call<UserDetails> updateUser(@QueryMap Map<String, String> params);
}