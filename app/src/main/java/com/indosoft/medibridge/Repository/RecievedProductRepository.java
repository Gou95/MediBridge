package com.indosoft.medibridge.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.indosoft.medibridge.Listener.RecentStockitslistener;
import com.indosoft.medibridge.Listener.RecievedOrderListener;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.Model.RecievedOrderResponse;
import com.indosoft.medibridge.RetrofitServices.ApiInterface;
import com.indosoft.medibridge.RetrofitServices.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecievedProductRepository {
    public static RecievedProductRepository repository;

    private final MutableLiveData<List<RecievedOrderResponse>> mutableLiveData = new MutableLiveData<>();


    // Singleton pattern
    public static RecievedProductRepository getInstance() {
        if (repository == null) {
            repository = new RecievedProductRepository();
        }
        return repository;
    }

    private final ApiInterface apiInterface;

    // Constructor
    public RecievedProductRepository() {
        apiInterface = RetrofitService.userService(ApiInterface.class);
    }

    public MutableLiveData<List<RecievedOrderResponse>> getRecieved(Context context, String retailer_id, RecievedOrderListener listener) {
        Call<List<RecievedOrderResponse>> call = apiInterface.getRecievedProduct(retailer_id);
        call.enqueue(new Callback<List<RecievedOrderResponse>>() {
            @Override
            public void onResponse(Call<List<RecievedOrderResponse>> call, Response<List<RecievedOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Failed to fetch data. Response is empty or null.");

                }
            }

            @Override
            public void onFailure(Call<List<RecievedOrderResponse>> call, Throwable t) {
                listener.onError("Something went wrong: " + t.getMessage());

            }
        });
        return mutableLiveData;
    }
}
