package com.av.ajouuniv.avproject2;

public class ApiClient
{
    public static final String BASE_URL = "http://testapi.droidmentor.com/";

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}