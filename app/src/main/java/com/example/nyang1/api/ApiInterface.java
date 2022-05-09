package com.example.nyang1.api;

import com.example.nyang1.category_search.CategoryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("v2/local/search/keyword.json")
    Call<CategoryResult> getSearchHospital(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("category_group_code") String category_group_code,
            @Query("x") String x,
            @Query("y") String y,
            @Query("radius") int radius
    );
}
