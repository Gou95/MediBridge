package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.OrderListener;
import com.indosoft.MediBridges.Listener.ProceedOrderListener;
import com.indosoft.MediBridges.Model.OrderResponse;
import com.indosoft.MediBridges.Model.ProceedOrderResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProceedOrderRepository {

    public static ProceedOrderRepository repository;

    private final MutableLiveData<ProceedOrderResponse> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static ProceedOrderRepository getInstance() {
        if (repository == null) {
            repository = new ProceedOrderRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public ProceedOrderRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<ProceedOrderResponse> proceedOrderData(Context context, String retailer_id, ProceedOrderListener listener) {
        Call<ProceedOrderResponse> call = apiInterface.proceedOrder(retailer_id);
        call.enqueue(new Callback<ProceedOrderResponse>() {
            @Override
            public void onResponse(Call<ProceedOrderResponse> call, Response<ProceedOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<ProceedOrderResponse> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
