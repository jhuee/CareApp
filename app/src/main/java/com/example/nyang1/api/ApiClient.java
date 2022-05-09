package com.example.nyang1.api;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String Base_URL = "https://dapi/kakao.com/";
    private static ApiClient instance = null;
    private static Retrofit retrofit;

    private ApiClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiClient getInstance(){
        if(instance == null){
            instance = new ApiClient();
        }
        return instance;
    }

    public Retrofit getApiClient() {
        return retrofit;
    }
}
