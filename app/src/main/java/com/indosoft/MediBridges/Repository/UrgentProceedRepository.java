package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.UrgentProceedListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.UrgentProceedResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UrgentProceedRepository {

    public static UrgentProceedRepository repository;

    private final MutableLiveData<UrgentProceedResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static UrgentProceedRepository getInstance() {
        if (repository == null) {
            repository = new UrgentProceedRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public UrgentProceedRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<UrgentProceedResponse> urgentProceedData(Context context, String retailer_id, UrgentProceedListener listener) {
        Call<UrgentProceedResponse> call = apiInterface.urgentProcced(retailer_id);
        call.enqueue(new Callback<UrgentProceedResponse>() {
            @Override
            public void onResponse(Call<UrgentProceedResponse> call, Response<UrgentProceedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<UrgentProceedResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }

}
