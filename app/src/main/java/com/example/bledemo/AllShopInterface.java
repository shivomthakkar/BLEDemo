package com.example.bledemo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AllShopInterface {

    @GET("gateway")
    Call<List<AllShopMappingResponse>> getMapping();

}
