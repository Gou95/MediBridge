package com.indosoft.GoodBooks.RetrofitServices;

import com.indosoft.GoodBooks.Model.CardListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("api.php")
    Call<List<CardListResponse>> cartList();
}
