package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.StockistListListener;
import com.indosoft.medibridge.Listener.StockitsListener;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockistListRepository {
    public static StockistListRepository repository;

    private final MutableLiveData<List<StockistListResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static StockistListRepository getInstance() {
        if (repository == null) {
            repository = new StockistListRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public StockistListRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<StockistListResponse>> getStockitsList(Context context, String city_id, StockistListListener listener) {
        Call<List<StockistListResponse>> call = apiInterface.stockistList(city_id);
        call.enqueue(new Callback<List<StockistListResponse>>() {
            @Override
            public void onResponse(Call<List<StockistListResponse>> call, Response<List<StockistListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<StockistListResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
