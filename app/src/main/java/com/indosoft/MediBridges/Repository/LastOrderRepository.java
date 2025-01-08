package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.GetUrgentCartListener;
import com.indosoft.MediBridges.Listener.LastOrderListener;
import com.indosoft.MediBridges.Model.GetUrgentCartResponse;
import com.indosoft.MediBridges.Model.LastOrderResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastOrderRepository {

    public static LastOrderRepository repository;

    private final MutableLiveData<List<LastOrderResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static LastOrderRepository getInstance() {
        if (repository == null) {
            repository = new LastOrderRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public LastOrderRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<LastOrderResponse>> getLastOrder(Context context, String retailer_id, LastOrderListener listener) {
        Call<List<LastOrderResponse>> call = apiInterface.lastOrder(retailer_id);
        call.enqueue(new Callback<List<LastOrderResponse>>() {
            @Override
            public void onResponse(Call<List<LastOrderResponse>> call, Response<List<LastOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LastOrderResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
