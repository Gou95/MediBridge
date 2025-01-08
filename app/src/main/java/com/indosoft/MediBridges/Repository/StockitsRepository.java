package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.StateListener;
import com.indosoft.MediBridges.Listener.StockitsListener;
import com.indosoft.MediBridges.Model.IndiaStateResponse;
import com.indosoft.MediBridges.Model.StockitsResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockitsRepository {

    public static StockitsRepository repository;

    private final MutableLiveData<List<StockitsResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static StockitsRepository getInstance() {
        if (repository == null) {
            repository = new StockitsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public StockitsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<StockitsResponse>> getStockitsOrder(Context context,String retailer_id ,String dealer_id, StockitsListener listener) {
        Call<List<StockitsResponse>> call = apiInterface.stockitsList(retailer_id,dealer_id);
        call.enqueue(new Callback<List<StockitsResponse>>() {
            @Override
            public void onResponse(Call<List<StockitsResponse>> call, Response<List<StockitsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<StockitsResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
