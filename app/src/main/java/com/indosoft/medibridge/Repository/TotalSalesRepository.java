package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.StockistListListener;
import com.indosoft.medibridge.Listener.TotalSalesListener;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.Model.TotalSalesResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalSalesRepository {
    public static TotalSalesRepository repository;

    private final MutableLiveData<List<TotalSalesResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static TotalSalesRepository getInstance() {
        if (repository == null) {
            repository = new TotalSalesRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public TotalSalesRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<TotalSalesResponse>> getTotalSales(Context context, String retailer_id, TotalSalesListener listener) {
        Call<List<TotalSalesResponse>> call = apiInterface.getTotal(retailer_id);
        call.enqueue(new Callback<List<TotalSalesResponse>>() {
            @Override
            public void onResponse(Call<List<TotalSalesResponse>> call, Response<List<TotalSalesResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {

                    if (listener != null) {
                        listener.onError("Failed to fetch data. Response is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<TotalSalesResponse>> call, Throwable t) {

                if (listener != null) {
                    listener.onError("Something went wrong: " + t.getMessage());
                }
            }
        });
        return mutableLiveData;
    }

}
