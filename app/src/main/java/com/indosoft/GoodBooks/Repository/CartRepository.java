package com.indosoft.GoodBooks.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.GoodBooks.Listener.CartListener;
import com.indosoft.GoodBooks.Model.CardListResponse;
import com.indosoft.GoodBooks.RetrofitServices.ApiInterface;
import com.indosoft.GoodBooks.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    public static CartRepository repository;

    private final MutableLiveData<List<CardListResponse>> mutableLiveData = new MutableLiveData<>();

    // Singleton pattern
    public static CartRepository getInstance() {
        if (repository == null) {
            repository = new CartRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public CartRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<CardListResponse>> getCartLiveData(Context context, CartListener listener) {
        Call<List<CardListResponse>> call = apiInterface.cartList();
        call.enqueue(new Callback<List<CardListResponse>>() {
            @Override
            public void onResponse(Call<List<CardListResponse>> call, Response<List<CardListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());
                    mutableLiveData.setValue(response.body());
                } else {
                    // Handle unsuccessful response (e.g., server error)
                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CardListResponse>> call, Throwable t) {
                // Handle failure (e.g., network issue)
                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }



}
