package com.indosoft.MediBridges.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.MediBridges.Listener.QuantityChangeListener;
import com.indosoft.MediBridges.Listener.RecentStockitslistener;
import com.indosoft.MediBridges.Model.QuantityChangeResponse;
import com.indosoft.MediBridges.Model.RecentStockitsResponse;
import com.indosoft.MediBridges.RetrofitServices.ApiInterface;
import com.indosoft.MediBridges.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentStockitsRepository {

    public static RecentStockitsRepository repository;

    private final MutableLiveData<List<RecentStockitsResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static RecentStockitsRepository getInstance() {
        if (repository == null) {
            repository = new RecentStockitsRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public RecentStockitsRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<RecentStockitsResponse>> getRecent(Context context, String retailer_id, RecentStockitslistener listener) {
        Call<List<RecentStockitsResponse>> call = apiInterface.recentStockits(retailer_id);
        call.enqueue(new Callback<List<RecentStockitsResponse>>() {
            @Override
            public void onResponse(Call<List<RecentStockitsResponse>> call, Response<List<RecentStockitsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<RecentStockitsResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
