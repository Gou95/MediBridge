package com.indosoft.mediBridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.mediBridge.Body.AddtoCartBody;
import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Listener.AddtoCartListener;
import com.indosoft.mediBridge.Listener.SignUpListener;
import com.indosoft.mediBridge.Model.AddtoCartResponse;
import com.indosoft.mediBridge.Model.SignUpResponse;
import com.indosoft.mediBridge.RetrofitServices.ApiInterface;
import com.indosoft.mediBridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddtoCartRepository {

    public static AddtoCartRepository repository;

    private final MutableLiveData<AddtoCartResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static AddtoCartRepository getInstance() {
        if (repository == null) {
            repository = new AddtoCartRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public AddtoCartRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<AddtoCartResponse> getAddCartData(Context context, AddtoCartBody body, AddtoCartListener listener) {
        Call<AddtoCartResponse> call = apiInterface.getAddToCartRes(body);
        call.enqueue(new Callback<AddtoCartResponse>() {
            @Override
            public void onResponse(Call<AddtoCartResponse> call, Response<AddtoCartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<AddtoCartResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
