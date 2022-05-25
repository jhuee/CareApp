package com.example.nyang1.api;

import com.example.nyang1.category_search.CategoryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {
    //장소이름으로 검색
    @GET("v2/local/search/keyword.json")
    Call<CategoryResult> getSearchLocation(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("size") int size
    );

    //카테고리로 검색
    @GET("v2/local/search/keyword.json")
    Call<CategoryResult> getSearchPetShop(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("x") String x,
            @Query("y") String y,
            @Query("radius") int radius
    );

    //장소이름으로 특정위치기준으로 검색
    @GET("v2/local/search/keyword.json")
    Call<CategoryResult> getSearchHospital(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("x") String x,
            @Query("y") String y,
            @Query("radius") int radius
    );


}