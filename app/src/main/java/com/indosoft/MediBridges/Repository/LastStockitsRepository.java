package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.CityListener;
import com.indosoft.MediBridges.Listener.LastStockitsListener;
import com.indosoft.MediBridges.Model.LastStockitsResponse;
import com.indosoft.MediBridges.Model.StateCityResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastStockitsRepository {

    public static LastStockitsRepository repository;

    private final MutableLiveData<List<LastStockitsResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static LastStockitsRepository getInstance() {
        if (repository == null) {
            repository = new LastStockitsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public LastStockitsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<LastStockitsResponse>> getLastStockits(Context context, String retailer_id,String product_id, LastStockitsListener listener) {
        Call<List<LastStockitsResponse>> call = apiInterface.lastStockits(retailer_id,product_id);
        call.enqueue(new Callback<List<LastStockitsResponse>>() {
            @Override
            public void onResponse(Call<List<LastStockitsResponse>> call, Response<List<LastStockitsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<LastStockitsResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
