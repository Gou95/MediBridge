package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.UrgentCartListener;
import com.indosoft.medibridge.Model.UrgentCartResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UrgentCartRepository {

    public static UrgentCartRepository repository;

    private final MutableLiveData<UrgentCartResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static UrgentCartRepository getInstance() {
        if (repository == null) {
            repository = new UrgentCartRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public UrgentCartRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<UrgentCartResponse> moveUrgentCartData(Context context,String cart_id, UrgentCartListener listener) {
        Call<UrgentCartResponse> call = apiInterface.urgentCart(cart_id);
        call.enqueue(new Callback<UrgentCartResponse>() {
            @Override
            public void onResponse(Call<UrgentCartResponse> call, Response<UrgentCartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<UrgentCartResponse> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
